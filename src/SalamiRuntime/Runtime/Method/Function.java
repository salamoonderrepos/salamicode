package SalamiRuntime.Runtime.Method;

import Logger.Logger;
import SalamiRuntime.Runtime.Value;

import java.util.ArrayList;
import java.util.List;

public interface Function {
    Value execute(List<Value> params, Logger logger);
}
