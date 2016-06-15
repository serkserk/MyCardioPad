package com.mycardiopad.g1.mycardiopad.util.services;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Réalisé par nicolassalleron le 18/01/16.
 * Envoyer une information vers la montre
 */
public class SendToDataLayerThread extends Thread {
    String path;
    String message;
    GoogleApiClient googleClient;

    // Constructeur pour envoyer un message vers la watch
    public SendToDataLayerThread(String p, String msg,GoogleApiClient googleClient) {
        path = p;
        message = msg;
        this.googleClient = googleClient;
    }

    //Le message est envoyé vers la montre
    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
            if (!result.getStatus().isSuccess())
                Log.v("Problème sur le mobile", " Impossible d'envoyer un message");
        }
    }
}