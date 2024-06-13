package com.mredust.oj.judge.codesandbox;

import com.mredust.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.mredust.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.mredust.oj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class CodeSandboxFactory {
    private CodeSandboxFactory() {
    }
    
    public static CodeSandbox newInstance(String type) {
        
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return null;
        }
    }
}
