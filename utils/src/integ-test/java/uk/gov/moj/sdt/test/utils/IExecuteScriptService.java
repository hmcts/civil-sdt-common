package uk.gov.moj.sdt.test.utils;

import org.springframework.core.io.Resource;

import java.util.concurrent.Future;

public interface IExecuteScriptService {

    public boolean executeScript(Resource script);

    public Future<Boolean> runScript(Resource script);

}
