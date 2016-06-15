package com.mycardiopad.g1.mycardiopad.util;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mycardiopad.g1.mycardiopad.database._Capture_Wear;

/**
 * Réalisé par nicolassalleron le 19/01/16.
 * Permet d'envoyer vers la montre  <br/>
 */
public class SendToDataLayerThread extends Thread {
    String path;
    String message;
    _Capture_Wear arrayListMesureValue;
    GoogleApiClient googleClient;
    byte[] bytes;

    /**
     * Envoyer une capture vers le mobile
     * @param p le canal du message
     * @param al la capture de session à envoyer
     * @param googleApiClient l'api pour envoyer
     */
    public SendToDataLayerThread(String p, _Capture_Wear al, GoogleApiClient googleApiClient) {
        this.path = p;
        this.arrayListMesureValue = al;
        this.googleClient = googleApiClient;

        message = al.toString();
        bytes = message.getBytes();
        Log.e("Message : "+bytes.length,message);
    }

    /**
     * Envoyer un message vers le mobile
     * @param p le canal du message
     * @param string le message à envoyer
     * @param googleApiClient l'api pour envoyer
     */
    // Constructor to send a message to the data layer
    public SendToDataLayerThread(String p, String string,GoogleApiClient googleApiClient) {
        //Log.e("SendToDataLayerThread",string);
        this.path = p;
        this.message = string;
        this.googleClient = googleApiClient;
        bytes = string.getBytes();

    }

    /**
     * Permet d'envoyer le message initiliser dans le constructeur
     */
    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        for (Node node : nodes.getNodes()) {
            //Log.e("Message : "+bytes.length,message);
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, bytes).await();
            if(result.getStatus().isSuccess()) {
                Log.v("WEAR", "Message: {" + message + "} sent to: " + node.getDisplayName());
            }
            else {
                Log.v("WEAR", "ERROR: failed to send Message");
            }
        }
    }
}