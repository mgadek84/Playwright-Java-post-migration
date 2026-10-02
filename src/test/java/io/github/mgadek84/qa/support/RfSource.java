package io.github.mgadek84.qa.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Traceability link from a migrated JUnit test to the Robot Framework test it replaces.
 * {@code tools/parity_report.py} uses it to match results of both suites one to one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RfSource {

    /** Suite file relative to the repository root, e.g. {@code tests/RF/API/Reqres_Auth.robot}. */
    String suite();

    /** Test case name exactly as written in the suite file. */
    String test();
}
