package cc.binmt.signature;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class PmsHookApplication extends Application implements InvocationHandler {
    private static final int GET_SIGNATURES = 64;
    private String appPkgName = "";
    private Object base;
    private byte[][] sign;

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context base2) {
        hook(base2);
        super.attachBaseContext(base2);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getPackageInfo".equals(method.getName())) {
            String pkgName = (String) args[0];
            int arg1 = Integer.valueOf((String) args[1]);
            if ((arg1 & 64) != 0 && this.appPkgName.equals(pkgName)) {
                PackageInfo info = (PackageInfo) method.invoke(this.base, args);
                info.signatures = new Signature[this.sign.length];
                for (int i = 0; i < info.signatures.length; i++) {
                    info.signatures[i] = new Signature(this.sign[i]);
                }
                return info;
            }
        }
        return method.invoke(this.base, args);
    }

    private void hook(Context context) {
        try {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(Base64.decode("AQAABYowggWGMIIDbqADAgECAhRUJB1MoLfriqDG8oqfXlQw4uczTDANBgkqhkiG9w0BAQsFADB0MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEUMBIGA1UEChMLR29vZ2xlIEluYy4xEDAOBgNVBAsTB0FuZHJvaWQxEDAOBgNVBAMTB0FuZHJvaWQwHhcNMTcwOTI4MTAwODI2WhcNNDcwOTI4MTAwODI2WjB0MQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzEUMBIGA1UEChMLR29vZ2xlIEluYy4xEDAOBgNVBAsTB0FuZHJvaWQxEDAOBgNVBAMTB0FuZHJvaWQwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC/KVPIsSTqSq9pO077n4Zb+xDw/bKGxekOK5FJCKznrKXxQxHybHyuguedNT+a/ywoF/xjzXVnkEEFNq3L/fTkgyfHkTGX98TfRq2B6Y2O3rrAYWKjXBPWqFM+ArWplhFMGkYgBwAN3WDrF/Dm6MXTRd/N45AFFaZg+KgKlYj20r5VrIeTqNTT0j1jVM1TPkPvpfjLtZCMii2kVE/zL13Nkk8bs2H5FhtB1bZHEPXQ0hMPoVB1FqdYVEk/lgJAQw39IYZ0+d8J60PFcvBJUZ1N+xbBNatgG54xNSUUFJkOBK3wxdbVH1sXlRQSLPvg6Jk042HHUtGvJ3JMMLc78rQtonx3qz4PJ+ierhtEXhRDQL+1C0m3PldOSZj3Cyxwt6Rn5MPAcoQ/SbSPumS0GrngF2v5M0+XJD81pavhJ4qItsUQN8g/Lleg5wlfdqciPDiybbE+vhhJPzMNyc0WKrhGQx07LK6bFqlqwPQlJOyVC7UyMsEftLVB00shT4QHLKN3lf3C0vDDjSMfFw8tSKFvSCUglODphNYoMiD11wn+Bzu5JJqqMbxxfzrAFNqV5XfbH6Bi0XHLhI0gSLB68/TMWKuFNxDt4m98O+3IyqekNQfO9Oa913J6RC5Y6vYuaHRmcFZvwf3eF5ByuvuA5SCL8NlLozAxErx/LqdBPf1HOwIDAQABoxAwDjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4ICAQA/5ZK+vARY+MsxbIgHwp8+ORYyA9/7r9JaHDViPQWeijjfStyYPD1gcicle8PFbyBI5/HHMrMQ0sVARCXTwruG2IQjuUM8ep2uNXrXlxX63OtMzmZfiLSNtnfMoMGtjykdvO+s+5pmbk2Tg3Tov6OwsOTM+r+BQp0YtKOJgrfZqw526rggs2WHZAUmRCb9wVnDvVz5RY0OItDY9fXrjh738CTOHlfhfaUwDjIAZYNGEUXNt0MIaWxSQP/erG83U7eTzZgjNhAquk31z0neW+cMMrOnA2m1lEsH6ZZ6PrfsVasHJHKBvHOoCO1+E+gZbsp3q+9G29UaXIInNgwyG1k+eYdGoJobOj0wqH70ggIMINa3i7YdHhBxgICxl4IkTeCQ/i+eounfLYGo6KxkeAgH3gyVR5PMk4ssdj6Mh2v2woMa+2DLkUfB13xoIfFG+M0XZb+gOdZfuZU9zC54ZtOgSzO+g/SFsSqAkg4cYaAsiNDv7SCqRP9rPejBo/8o1G2QOmhFcIDxpIKML5TJjTWqeP+tLiX0gDEi2FKp/oFgRRacvncqlBa5hUerkI0SOOcz/Qsoi9OHXAgNAIK1+f0fCnpy53Ythp9EqA5l6Spv9SVkZ/8snW+QEq8ivjzEtEIMd5FbmS48UFkhIx9JH8pGMhBMKfTt9te6e5+p4nzrxw==", 0)));
            byte[][] sign2 = new byte[(is.read() & 255)][];
            for (int i = 0; i < sign2.length; i++) {
                sign2[i] = new byte[is.readInt()];
                is.readFully(sign2[i]);
            }
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]);
            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            this.base = sPackageManager;
            this.sign = sign2;
            this.appPkgName = context.getPackageName();
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(), new Class[]{iPackageManagerInterface}, this);
            sPackageManagerField.set(currentActivityThread, proxy);
            PackageManager pm = context.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);
            System.out.println("PmsHook success.");
        } catch (Exception e) {
            System.err.println("PmsHook failed.");
            e.printStackTrace();
        }
    }
}
