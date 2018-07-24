package com.ncku.iir.computex.core;

import android.content.Context;

public interface IRequest {
    public Context getContext();
    public void  onGetMessage(String text);
}
