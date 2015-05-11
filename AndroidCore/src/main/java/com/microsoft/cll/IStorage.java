package com.microsoft.cll;

import com.microsoft.telemetry.IJsonSerializable;

import java.util.List;

public interface IStorage {
        public void add(IJsonSerializable event) throws Exception;

        public void add(String event) throws Exception;

        public boolean canAdd(IJsonSerializable event);

        public boolean canAdd(String event);

        public List<String> drain();

        public long size();

        public void discard();

        public void close();
}
