package com.imoonday.compression_bag;

import net.fabricmc.api.ClientModInitializer;

public class CompressionBagClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CompressionBagItem.registerClient();
    }
}
