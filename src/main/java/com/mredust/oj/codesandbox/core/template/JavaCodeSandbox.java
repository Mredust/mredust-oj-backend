package com.mredust.oj.codesandbox.core.template;

import cn.hutool.core.io.FileUtil;
import com.mredust.oj.codesandbox.constant.JavaConstant;
import com.mredust.oj.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.oj.codesandbox.model.dto.ExecuteResult;
import com.mredust.oj.codesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
public class JavaCodeSandbox extends CodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(List<String> inputList, String code) {
        return super.executeCode(inputList, code);
    }
    
    @Override
    protected File saveCodeToFile(String path, String code) {
        String codePath = String.format("%s%s%s%s%s%s%s", path, File.separator,
                JavaConstant.JAVA, File.separator, UUID.randomUUID(), File.separator, JavaConstant.JAVA_DEFAULT_NAME);
        return FileUtil.writeUtf8String(code, codePath);
    }
    
    @Override
    protected ExecuteResult compileCode(File file) throws IOException {
        String compileCmd = String.format(JavaConstant.JAVA_COMPILE_CMD, file.getAbsolutePath());
        Process compileProcess = Runtime.getRuntime().exec(compileCmd);
        return ProcessUtils.processHandler(compileProcess);
    }
    
    @Override
    protected List<ExecuteResult> runCompileFile(String cmd, File file, String path, List<String> inputList) throws IOException {
        return super.runCompileFile(JavaConstant.JAVA_RUN_CMD, file, file.getParent(), inputList);
    }
}
