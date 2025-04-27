package SalamiRuntime.Runtime.Method;

import Helper.Logger.Logger;
import SalamiRuntime.Runtime.Value;

import java.util.List;

public interface Function {
    Value execute(List<Value> params, Logger logger);
}
