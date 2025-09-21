package icu.nullptr.hidemyapplist.common;

interface IHMAService {

    void stopService(boolean cleanEnv) = 0;

    void writeConfig(String json) = 1;

    int getServiceVersion() = 2;

    int getFilterCount() = 3;

    String getLogs() = 4;

    void clearLogs() = 5;

    void handlePackageEvent(String eventType, String packageName) = 6;

    String[] getPackagesForPreset(String presetName) = 7;

    String readConfig() = 8;
}
