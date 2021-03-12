package com.example.mvvmarchitectureexample.util;

import android.os.Build;
import android.os.SystemClock;
import android.util.ArrayMap;

import androidx.annotation.RequiresApi;

import java.util.concurrent.TimeUnit;

//Clase que posee un tiempo predefinido:
//  -Si paso el limite de tiempo, solicita datos al servidor(remoto)
//  -Si no paso el limite de tiempo, seguira utilizando los datos de la base local(local)
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RateLimiter<KEY> {
    private ArrayMap<KEY, Long> timestamp = new ArrayMap<>();
    private final Long timeout;

    public RateLimiter(int timeout, TimeUnit timeUnit) {
        this.timeout = timeUnit.toMillis(timeout);
    }

    public synchronized boolean shouldFetched(KEY key) {
        boolean shouldFetched = false;
        Long lastFetched = this.timestamp.get(key);
        Long now = SystemClock.uptimeMillis();

        if(lastFetched == null) {
            timestamp.put(key, now);
            shouldFetched = true;
        } else if((now - lastFetched) > timeout) {
            timestamp.put(key, now);
            shouldFetched = true;
        }

        return shouldFetched;
    }

    public synchronized void reset(KEY key) {
        this.timestamp.remove(key);
    }
}
