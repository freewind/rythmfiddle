package jobs;

import common.CodeManager;
import models.Code;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.IOException;

/**
 * User: freewind
 * Date: 13-3-21
 * Time: 下午7:11
 */
@OnApplicationStart
public class InitJobs extends Job {

    /**
     * Here you do the job
     */
    @Override
    public void doJob() throws Exception {
        CodeManager.CODE_ROOT.mkdirs();
        CodeManager.reload();
        Code.initRythmEngine();
    }
}
