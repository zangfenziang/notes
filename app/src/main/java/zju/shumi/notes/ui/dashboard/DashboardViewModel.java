package zju.shumi.notes.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

public class DashboardViewModel extends ViewModel {
    private MutableLiveData<Set<String>> mFileNames;
    public DashboardViewModel(){
        mFileNames = new MutableLiveData<>();
        mFileNames.setValue(new HashSet<String>());
    }
    public LiveData<Set<String>> getFileNames(){
        return mFileNames;
    }

    public void setFileNames(Set<String> mFileNames) {
        this.mFileNames.setValue(mFileNames);
    }

    public void addFileName(String filename){
        Set<String> set = this.mFileNames.getValue();
        set.add(filename);
        setFileNames(set);
    }
}