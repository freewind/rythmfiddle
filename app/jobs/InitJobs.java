package jobs;

import common.CodeManager;
import demo.Bar;
import demo.Foo;
import demo.Order;
import demo.User;
import org.rythmengine.logger.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import org.rythmengine.play.RythmPlugin;

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
        loadModelClassesBeforeHand();
        //Code.initRythmEngine();
    }

    private void loadModelClassesBeforeHand() {
        new Bar();
        new Foo();
        new Order();
        new User();
        RythmPlugin.render("@args demo.Bar bar, demo.Foo foo, demo.Order order, demo.User user");
        Logger.info("model classes loaded");
    }

}
