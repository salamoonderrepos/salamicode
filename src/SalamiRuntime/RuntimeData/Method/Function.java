package SalamiRuntime.RuntimeData.Method;

import Helper.Logger.Logger;
import SalamiRuntime.RuntimeData.Value;

import java.util.List;

public interface Function {
    Value execute(List<Value> params, Logger logger);
}
