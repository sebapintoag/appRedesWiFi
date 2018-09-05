package redescomputacionales.cl.appredeswifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class ListAdapter extends BaseAdapter {
    Context context;
    String dataList[][];
    LayoutInflater inflter;

    public ListAdapter(Context applicationContext, String[][] dataList) {
        this.context = context;
        this.dataList = dataList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return dataList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_item, null);

        TextView id_number = (TextView) view.findViewById(R.id.id_number);
        TextView latitud = (TextView) view.findViewById(R.id.latitud);
        TextView longitud = (TextView) view.findViewById(R.id.longitud);
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        TextView velocidad = (TextView) view.findViewById(R.id.velocidad);
        TextView intensidad = (TextView) view.findViewById(R.id.intensidad);
        TextView estado = (TextView) view.findViewById(R.id.estado);

        id_number.setText(dataList[i][0]);
        latitud.setText(dataList[i][1]);
        longitud.setText(dataList[i][2]);
        fecha.setText(dataList[i][3]);
        velocidad.setText(dataList[i][4] + " Kbps");
        intensidad.setText(dataList[i][5] + " dBm");
        estado.setText(dataList[i][6]);

        return view;
    }
}
