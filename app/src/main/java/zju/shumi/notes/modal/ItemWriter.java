package zju.shumi.notes.modal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ItemWriter {
    public static void write(File file, ArrayList<Item> items)throws Exception{
        StringBuilder builder = new StringBuilder();
        if (items.size() > 0){
            builder.append(items.get(0).toString());
            for (int i = 1; i < items.size(); i++) {
                Item item = items.get(i);
                if (item.getDeep() == 0){
                    builder.append("……\n");
                }
                builder.append(item.toString());
            }
        }
        FileOutputStream os = new FileOutputStream(file);
        os.write(builder.toString().getBytes());
        os.close();
    }
}
