package com.whatstatus;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Make an http request, T1: data to send, T2: data to recieve
 */
public class HttpRequest {
    // Data Members
    private String m_reqUrl;
    private String m_reqAction;
    private HashMap<String, String> m_reqData;

    public HttpRequest(String reqAction, HashMap<String, String> reqData, String reqUrl) {
        this.setAction(reqAction);
        this.setData(reqData);
        this.setUrl(reqUrl);
    }

    public String getUrl() {
        return m_reqUrl;
    }

    public void setUrl(String m_reqUrl) {
        this.m_reqUrl = m_reqUrl;
    }

    public String getAction() {
        return m_reqAction;
    }

    public void setAction(String m_reqAction) {
        this.m_reqAction = m_reqAction;
    }

    public HashMap<String, String> getData() {
        return m_reqData;
    }

    public void setData(HashMap<String, String> m_reqData) {
        this.m_reqData = m_reqData;
    }

    public AsyncTask<Void, Void, String> execute() {
        return new HttpRequestTask().execute();
    }

    public class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void ...Params) {
            URL url;
            String response = "";
            try {
                url = new URL(getUrl());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                String postData = "action=" + getAction() + "&";

                if (getData() != null) {
                    for (String key : getData().keySet()) {
                        postData.concat(key + "=" + getData().get(key) + "&");
                    }
                }

                postData = postData.substring(0, postData.length() - 1);

                writer.write(URLEncoder.encode(postData, "UTF-8"));

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response = "Error";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
    }
}
