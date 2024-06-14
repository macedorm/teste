package br.com.jjconsulting.mobile.jjlib.dao.entity;


import java.util.ArrayList;
import java.util.List;

public class FormElementList extends ElementField {

    private List<FormElementField> formFields;

    //private ElementList baseFields;

    public FormElementList()
    {
       // baseFields = new ElementList();
        formFields = new ArrayList<>();
    }

    //public FormElementList(/*ElementList baseFields*/, List<FormElementField> formFields)
    public FormElementList(List<FormElementField> formFields)
    {
      //  _BaseFields = baseFields;
        this.formFields = formFields;
    }

    public void add(FormElementField item)
    {
        //_BaseFields.Add(item);
        formFields.add(item);
    }

    public void clear()
    {
        //_BaseFields.Clear();
        formFields.clear();
    }


    public boolean contains(String fieldName)
    {
        for (FormElementField val:formFields) {
            {
                if (val.getFieldname().equals(fieldName))
                    return true;
            }
        }
        return false;
    }

    public boolean contains(FormElementField item)
    {
        return formFields.contains(item);
    }

    public void CopyTo(List<FormElementField> array, int arrayIndex)
    {
        //_BaseFields.CopyTo(array.ToArray<ElementField>(), arrayIndex);
        formFields.addAll(arrayIndex, array);
    }

    public boolean remove(FormElementField item)
    {
       // _BaseFields.Remove(item);
        return formFields.remove(item);
    }

    public int count(){
        return formFields.size();
    }

    public boolean isReadOnly(){
        //return formFields.isReadOnly; }
        return true;
    }

    public void set(FormElementField formElementFields, int index){
        formFields.set(index, formElementFields);
        //_BaseFields[index] = value;
    }

    public FormElementField get(int index)
    {
        return formFields.get(index);
    }

    public FormElementField get(String fieldName)
    {
        FormElementField value = null;

        for (FormElementField val: formFields) {
            FormElementField e = (FormElementField) val;
            if (e.getFieldname().equals(fieldName)) {
                value = val;
            }

            throw new IllegalArgumentException(String.format("value %s not found", fieldName));
        }

        return value;
    }

    public FormElementField set(String fieldName)
    {
        FormElementField value = null;

        boolean isOk = false;
        for (int i = 0; i < formFields.size(); i++)
        {
            FormElementField e = formFields.get(i);
            if (e.getFieldname().equals(fieldName))
            {
                 formFields.set(i,value);
                //_BaseFields[i] = (FormElementField)value;
                isOk = true;
                break;
            }
        }
        if (!isOk)
            throw new IllegalArgumentException(String.format("value %s not found", fieldName));

        return value;
    }


    public int indexOf(String fieldName)
    {
        int index = -1;
        for (int i = 0; i < formFields.size(); i++)
        {
            if (formFields.get(i).getFieldname().equals(fieldName))
            {
                index = i;
                break;
            }
        }
        return index;
    }
}
