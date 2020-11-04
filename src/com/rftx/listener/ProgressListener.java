package com.rftx.listener;

import com.rftx.core.FileTaskInfo;

public interface ProgressListener {
    /**
     * call when transport conn update buffer data
     */
    void progressUpdate(FileTaskInfo info);
}
