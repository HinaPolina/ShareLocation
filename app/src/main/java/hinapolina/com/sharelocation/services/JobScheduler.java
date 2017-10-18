package hinapolina.com.sharelocation.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by polina on 10/17/17.
 */

public class JobScheduler  extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
