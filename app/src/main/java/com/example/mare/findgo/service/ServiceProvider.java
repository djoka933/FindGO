package com.example.mare.findgo.service;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class ServiceProvider {

    private static String TAG = "ServiceProvider";
    public static final int GET_METHOD = 0;
    public static final int POST_METHOD = 1;
    public static final int PUT_METHOD = 2;
    public static final int DELETE_METHOD = 3;
    private static int mTimeoutConnection = 30000;
    private static int mTimeoutSocket = 60000;

    //private static String mServiceUrl = "http://192.168.0.136:49415/AndroidService.svc/droid";

    private static String mServiceUrl = "http://192.168.0.136:49415/AndroidService.svc/droid";

    private ServiceProvider() {
        throw new AssertionError();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private static HttpParams GetMyHttpParameters() {
        HttpParams httpParameters = new BasicHttpParams();
        // int timeoutConnection = 3000;
        // int timeoutSocket = 5000;
        // try {
        // timeoutConnection = Integer.parseInt(context
        // .getString(R.string.timeoutConnection));
        // timeoutSocket = Integer.parseInt(context
        // .getString(R.string.timeoutSocket));
        // } finally {
        // // set default values;
        // }
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                mTimeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, mTimeoutSocket);
        return httpParameters;
    }

    public static HttpClient GetMyHttpClient() {
        HttpParams params = GetMyHttpParameters();
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", PlainSocketFactory
                .getSocketFactory(), 80));
        registry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
                registry);
        HttpClient client = new DefaultHttpClient(cm, params);
        return client;
    }

    public static String GenericHttpMethod(Context context, int operation,
                                           String serviceUrl, String secToken) throws Exception {
        return GenericHttpMethod(context, operation, serviceUrl, secToken, null);
    }

    public static String GenericHttpMethod(Context context, int operation,
                                           String serviceUrl, String secToken, String jsonMessageBody)
            throws Exception {
        Log.d(TAG, "Generic HTTP method, operation " + operation + "; "
                + serviceUrl);

        if (!isOnline(context))
            throw new ConnectionClosedException(
                    "No internet connection, please try later");
        String json = null;
        HttpClient client = GetMyHttpClient();
        HttpResponse response = null;

        StringEntity se = null;
        if (jsonMessageBody != null) {
            se = new StringEntity(jsonMessageBody, HTTP.UTF_8);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
        }

        switch (operation) {
            case GET_METHOD:
                HttpGet get = new HttpGet(serviceUrl);
                get.addHeader("Authorization", "Bearer " + secToken);
                response = client.execute(get);
                break;
            case POST_METHOD:
                HttpPost post = new HttpPost(serviceUrl);
                post.addHeader("Authorization", "Bearer " + secToken);
                if (se != null) {
                    post.addHeader("Content-type", "application/json");
                    post.setEntity(se);
                }
                response = client.execute(post);
                break;
            case PUT_METHOD:
                HttpPut put = new HttpPut(serviceUrl);
                put.addHeader("Authorization", "Bearer " + secToken);
                if (se != null) {
                    put.addHeader("Content-type", "application/json");
                    put.setEntity(se);
                }
                response = client.execute(put);
                break;
            case DELETE_METHOD:
                HttpDelete delete = new HttpDelete(serviceUrl);
                delete.addHeader("Authorization", "Bearer " + secToken);
                response = client.execute(delete);
                break;
        }

        if (IsResponseValid(response)) {

            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(entity);

        } else {
            // if (IsTokenExpired(response)) {
            // throw new TokenExpiredException("Token expired");
            // // InvalidateToken(context, secToken);
            // // return GetStatusesStream(context,
            // // account,userId,pageNo,pageSize,lastStatusId);
            // } else {
            HandleUnexpectedHttpError(response);
            // }
        }

        return json;
    }

    static boolean IsResponseValid(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200 || statusCode == 201 || statusCode == 225)
            return true;
        else
            return false;
    }

    static void HandleUnexpectedHttpError(HttpResponse response)
            throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        String reason = response.getStatusLine().getReasonPhrase();
        throw new Exception("Trouble reading status(code=" + statusCode + "):"
                + reason);
    }

    public static String GetNewMessages(Context context, String sellerId,
                                        String lastMessageId) throws Exception {
        String secToken = null;
        String serviceUrl = mServiceUrl + "/administration/newmessages/"
                + sellerId + "/" + lastMessageId;

        String json = GenericHttpMethod(context, GET_METHOD, serviceUrl,
                secToken);
        return json;

    }

    public static int AuthenticateUser(Context context, String username,
                                       String password) throws Exception {
        String secToken = null;
        String serviceUrl = mServiceUrl + "/users/authenticate";

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("Username", username);
        jsonObj.put("Password", password);

        String result = GenericHttpMethod(context, POST_METHOD, serviceUrl,
                secToken, jsonObj.toString());
        return Integer.parseInt(result);
    }

    public static boolean ChangeUserPassword(Context context, String username,
                                             String password, String newPassword)
            throws Exception {
        String secToken = null;
        String serviceUrl = mServiceUrl + "/users/password";

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("Username", username);
        jsonObj.put("Password", password);
        jsonObj.put("NewPassword", newPassword);

        String result = GenericHttpMethod(context, POST_METHOD, serviceUrl,
                secToken, jsonObj.toString());
        return Boolean.parseBoolean(result);
    }

    public static String CheckUsername(Context context, String username) throws Exception {
        // String secToken = null;
        String serviceUrl = mServiceUrl + "/products/checkprices";

        HttpClient client = GetMyHttpClient();
        HttpResponse response = null;
        HttpPost post = new HttpPost(serviceUrl);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("username", username);
        post.addHeader("Content-type", "application/json");
        StringEntity se = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        post.setEntity(se);
        response = client.execute(post);

        String json = null;
        if (IsResponseValid(response)) {

            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(entity);

        } else {
            // if (IsTokenExpired(response)) {
            // throw new TokenExpiredException("Token expired");
            // // InvalidateToken(context, secToken);
            // // return GetStatusesStream(context,
            // // account,userId,pageNo,pageSize,lastStatusId);
            // } else {
            HandleUnexpectedHttpError(response);
            // }
        }
        return json;
    }

    public static String SignInUser(Context applicationContext,
                                        String username, String password, String name,
                                        String adress, String birth, String phone, String path)
            throws Exception {
        String serviceUrl = mServiceUrl + "/users/new";

        String photoString;
        photoString = GetBitmapStringFromPath(path);

        HttpClient client = GetMyHttpClient();
        HttpResponse response = null;
        HttpPost post = new HttpPost(serviceUrl);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("UserName", username);
        jsonObj.put("Password", password);
        jsonObj.put("Name", name);
        jsonObj.put("Adress", adress);
        jsonObj.put("BirthDay", birth);
        jsonObj.put("PhoneNumber", phone);
        jsonObj.put("Image", photoString);

        // post.addHeader("Authorization", "Bearer " + secToken);
        post.addHeader("Content-type", "application/json");
        StringEntity se = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        post.setEntity(se);
        response = client.execute(post);

        String json = null;
        if (IsResponseValid(response)) {

            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(entity);

        } else {
            // if (IsTokenExpired(response)) {
            // throw new TokenExpiredException("Token expired");
            // // InvalidateToken(context, secToken);
            // // return GetStatusesStream(context,
            // // account,userId,pageNo,pageSize,lastStatusId);
            // } else {
            HandleUnexpectedHttpError(response);
            // }
        }
        return json;
    }

    public static String GetBitmapStringFromPath(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap imageBitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoByteArr;
        photoByteArr = baos.toByteArray();
        String encoded = Base64.encodeToString(photoByteArr, Base64.DEFAULT);
        return encoded;
    }
}
