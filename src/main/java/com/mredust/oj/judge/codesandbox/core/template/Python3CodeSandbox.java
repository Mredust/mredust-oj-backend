package com.mredust.oj.judge.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.oj.judge.codesandbox.utils.ProcessUtils;
import com.mredust.oj.exception.CompilationException;
import com.mredust.oj.judge.codesandbox.constant.PythonConstant;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mredust.oj.judge.codesandbox.constant.CodeSandboxConstant.INIT_VALUE;
import static com.mredust.oj.judge.codesandbox.constant.CodeSandboxConstant.PROBLEM_CLASS_NAME;


/**
 * Python代码沙箱
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */

public class Python3CodeSandbox extends CodeSandboxTemplate {
    private static final String[] ERROR_MESSAGE_LIST = {"Traceback", "Error", "错误", "异常"};
    
    @Override
    protected File preprocessFile(String parentPath, String code) {
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (PROBLEM_CLASS_NAME + PythonConstant.PYTHON_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    
    @Override
    protected String generateTemplateCode(File file) {
        StringBuilder templateCode = new StringBuilder();
        String rootPath = System.getProperty("user.dir");
        Path pythonFilePath = Paths.get(rootPath, "src", "main", "resources", PythonConstant.PYTHON_METHOD_INFO_EXTRACTOR);
        String cmd = String.format("python %s %s", pythonFilePath, file.getAbsolutePath());
        String methodInfo = ProcessUtils.processHandler(cmd, INIT_VALUE, INIT_VALUE);
        if (!methodInfo.isEmpty() && Arrays.stream(ERROR_MESSAGE_LIST).anyMatch(methodInfo::contains)) {
            methodInfo = getErrorMessage(Collections.singletonList(methodInfo));
            clearFile(file);
            throw new CompilationException(methodInfo);
        }
        String[] pyJson = methodInfo.split(" ");
        String name = pyJson[0].split(":")[1].trim();
        String[] args = {};
        String[] types = pyJson[1].split(":");
        if (types.length > 1) {
            args = pyJson[1].split(":")[1].split(",");
        }
        templateCode.append("\n\ndef type_conversion(arg, arg_type):\n")
                .append("\tif arg_type == \"int\":\n")
                .append("\t\treturn int(arg)\n")
                .append("\telif arg_type == \"float\":\n")
                .append("\t\treturn float(arg)\n")
                .append("\telif arg_type == \"str\":\n")
                .append("\t\treturn str(arg)\n")
                .append("\telif arg_type.startswith(\"List[\") and arg_type.endswith(\"]\"):\n")
                .append("\t\treturn [type_conversion(item.strip(), arg_type[5:-1]) for item in arg.split(',')]\n")
                .append("\telse:\n")
                .append("\t\traise ValueError(f\"不支持该类型的参数: {arg_type}\")\n\n");
        templateCode.append("if __name__ == '__main__':\n")
                .append("\timport sys\n");
        templateCode.append("\tprint(str(Solution().").append(name).append("(");
        for (int i = 0; i < args.length; i++) {
            templateCode.append("type_conversion(sys.argv[").append(i + 1).append("], '").append(args[i].trim()).append("')");
            if (i < args.length - 1) {
                templateCode.append(", ");
            }
        }
        templateCode.append(")).strip('[]'))\n");
        return templateCode.toString();
    }
    
    @Override
    protected String mergeCode(String templateCode, String code) {
        return PythonConstant.PYTHON_TYPING_PACKAGE + code + templateCode;
    }
    
    @Override
    protected File saveFile(String code, String parentPath, String fileName) {
        String filePath = String.format("%s%s%s%s%s", parentPath, File.separator, UUID.randomUUID(), File.separator, (PROBLEM_CLASS_NAME + PythonConstant.PYTHON_SUFFIX));
        return FileUtil.writeUtf8String(code, filePath);
    }
    
    @Override
    protected List<String> runCode(File file, List<String> testCaseList, Long[] time, Long[] memory) {
        List<String> list = new ArrayList<>();
        for (String args : testCaseList) {
            List<String> params = new ArrayList<>();
            params.add(PythonConstant.PYTHON_RUN_CMD);
            params.add(file.getAbsolutePath());
            params.add(args);
            String cmd = String.join(" ", params);
            String msg = ProcessUtils.processHandler(cmd, time, memory);
            list.add(msg);
        }
        return list;
    }
    
    @Override
    protected String getErrorMessage(List<String> runMessageList) {
        String regex = "line.*?(?=(\\\\|$|\\n))";
        Pattern pattern = Pattern.compile(regex);
        for (String msg : runMessageList) {
            if (Arrays.stream(ERROR_MESSAGE_LIST).anyMatch(msg::contains)) {
                Matcher matcher = pattern.matcher(msg);
                String lastMatch = null;
                while (matcher.find()) {
                    lastMatch = matcher.group();
                }
                if (lastMatch != null) {
                    return lastMatch + "\n";
                } else {
                    return "";
                }
            }
        }
        return "";
    }
    
}
