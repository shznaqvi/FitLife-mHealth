package edu.aku.hassannaqvi.fitlife.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;

public class WorkerStatusViewModel extends ViewModel {

    // LiveData to hold the tagStatuses
    public MutableLiveData<WorkInfo.State[]> tagStatuses = new MutableLiveData<>();

    // Function to update the tagStatusesLiveData
    public void updateTagStatuses(WorkInfo.State[] statuses) {
        tagStatuses.setValue(statuses);
    }
}