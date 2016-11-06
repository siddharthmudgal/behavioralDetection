package com.ezmcom.smudgal.walkpattern;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import static com.ezmcom.smudgal.walkpattern.Constants.*;


/**
 * Created by SMudgal on 10/18/2016.
 */

public class DetectedActivityService extends IntentService {

    protected static final String name = "DetectedActivitiesIS";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public DetectedActivityService() {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult activityRecognitionResult = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();
            Intent broadcast_user_movement_intent = new Intent(movement);;
            if (DetectedActivity.WALKING == detectedActivity.getType() ||
                    (DetectedActivity.ON_FOOT == detectedActivity.getType() &&
                            activityRecognitionResult.getActivityConfidence(DetectedActivity.WALKING) >
                            activityRecognitionResult.getActivityConfidence(DetectedActivity.RUNNING))){
                broadcast_user_movement_intent.putExtra("style",walking);
            }
            else if (DetectedActivity.RUNNING == detectedActivity.getType()||
                    (DetectedActivity.ON_FOOT == detectedActivity.getType() &&
                            activityRecognitionResult.getActivityConfidence(DetectedActivity.WALKING) <
                            activityRecognitionResult.getActivityConfidence(DetectedActivity.RUNNING))){
                broadcast_user_movement_intent.putExtra("style",running);
            }
            if (broadcast_user_movement_intent != null)
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast_user_movement_intent);
        }
    }
}
