package SalamiPackager.Packages.math;

import Logger.Logger;
import SalamiPackager.Packages.Package;
import SalamiRuntime.Runtime.Environment;
import SalamiRuntime.Runtime.Method.Function;
import SalamiRuntime.Runtime.Value;

import java.util.List;

public class MathPackage implements Package {
    public void initialize(Environment env) {
        env.declareMethod("add", List.of(Value.class, Value.class), (params, logger) -> null);
    }
}

