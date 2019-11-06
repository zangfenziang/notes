package zju.shumi.notes.modal;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ItemsReader {
    public static ArrayList<Item> read(File file) throws Exception{
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buff = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while(((len = fileInputStream.read(buff)) > 0)){
            bos.write(buff, 0, len);
        }
        fileInputStream.close();
        String str = bos.toString();
        String[] data = str.split("……\n");
        ArrayList<Item> items = new ArrayList<>();
        for (String datum : data) {
            String[] value = datum.split("\n");
            for (int i = 0; i < value.length / 3; ++i){
                items.add(
                        Item.parse(
                                String.format(
                                        "%s\n%s\n%s", value[i * 3], value[i * 3 + 1], value[i * 3 +2]
                                )
                        )
                );
            }
        }
        return items;
    }
}
