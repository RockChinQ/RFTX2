package com.rftx.listener;

import com.rftx.core.FileTaskInfo;

public interface TaskListener {
    void start(FileTaskInfo info);
    void finish(FileTaskInfo info);
    void interrupt(FileTaskInfo info);
}
