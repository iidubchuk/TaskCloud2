package com.testing.iidubchuk.taskcloud;

/**
 * Created by root on 18.02.18.
 */

public class Task {
    public String Id;
    public String TaskName;

    public Task() {
    }

    public Task(String id, String taskName) {
        Id = id;
        TaskName = taskName;

    }

    public Task(int id, String taskName) {
        Id = String.valueOf(id);
        TaskName = taskName;
    }

    @Override
    public String toString() {
        return TaskName;
    }

    public int GetIdInt() {
        return Integer.parseInt(Id);
    }


}
