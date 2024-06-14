package br.com.jjconsulting.mobile.jjlib.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.jjconsulting.mobile.jjlib.R;

public abstract class SpinnerArrayAdapter<T> extends ArrayAdapter<Object> {

    private String mHint;
    private boolean mUseHint;

    public SpinnerArrayAdapter(@NonNull Context context, @NonNull Object[] objects, boolean useHint) {
        super(context, R.layout.simple_item_spinner, objects);

        mUseHint = useHint;

        if (mUseHint) {
            mHint = (String) objects[0];
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setPadding(16, 0, 0, view.getPaddingBottom());

        TextView textView = (TextView) view;

        if (textView == null) {
            return view;
        }

        Object item = getItem(position);

        // if it's the header (String at index 0)
        if (position == 0 && mUseHint) {
            textView.setText(mHint);
            return textView;
        }

        T convertedItem = (T) item;

        // if it' not the desired class
        if (convertedItem == null) {
            textView.setText(item.toString());
            return textView;
        }

        textView.setText(getItemDescription(convertedItem));
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;

        if (textView == null) {
            return view;
        }

        Object item = getItem(position);

        // if it's the header (String at index 0)
        if (position == 0 && mUseHint) {
            textView.setText(mHint);
            return textView;
        }

        T convertedItem = (T) item;

        // if it' not the desired class
        if (convertedItem == null) {
            textView.setText(item.toString());
            return textView;
        }

        textView.setText(getItemDescription(convertedItem));
        return textView;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        if (position == 0 && mUseHint) {
            return String.valueOf(hashCode());
        }

        return super.getItem(position);
    }

    public abstract String getItemDescription(T item);

    public boolean isThereAnyItemSelected(Spinner spinner) {
        Object selectedItem = spinner.getSelectedItem();

        if (selectedItem == null) {
            return false;
        }

        if (!selectedItem.getClass().equals(String.class)) {
            return true;
        }

        return !selectedItem.equals(String.valueOf(hashCode()));
    }

    public static Object[] makeObjectsWithHint(int[] objects, String hint) {
        Object[] objectsWithHint = new Object[objects.length + 1];
        objectsWithHint[0] = hint;
        for (int i = 1; i < objectsWithHint.length; i++) {
            objectsWithHint[i] = Integer.valueOf(objects[i - 1]);
        }

        return objectsWithHint;
    }

    public static Object[] makeObjectsWithHint(Object[] objects, String hint) {
        Object[] objectsWithHint = new Object[objects.length + 1];
        objectsWithHint[0] = hint;
        for (int i = 1; i < objectsWithHint.length; i++) {
            objectsWithHint[i] = objects[i - 1];
        }

        return objectsWithHint;
    }

    public static Object[] makeObjects(Object[] objects) {
        Object[] objectsWithHint = new Object[objects.length];
        for (int i = 1; i < objectsWithHint.length; i++) {
            objectsWithHint[i] = objects[i - 1];
        }

        return objectsWithHint;
    }
}
