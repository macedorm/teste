package br.com.jjconsulting.mobile.dansales.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePage;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class HierarquiaComercialViewHolder extends TreeNode.BaseNodeViewHolder<Object> {

    private Class mType;
    private CheckBox mSelecaoCheckBox;
    private TextView mNomeUsuarioTextView;
    private TextView mEmailUsuarioTextView;
    private TextView mPaginaTextView;
    private ImageView mArrowImageView;

    public HierarquiaComercialViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, Object value) {
        mType = value.getClass();
        if (mType.equals(Usuario.class)) {
            return createNodeViewForUsuario(node, (Usuario) value);
        } else if (mType.equals(TreeNodePage.class)) {
            return createNodeViewForPage(node, (TreeNodePage) value);
        } else {
            return null;
        }
    }

    private View createNodeViewForUsuario(TreeNode node, Usuario usuario) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_hierarquia_comercial_funcionario, null,
                false);

        mSelecaoCheckBox = view.findViewById(
                R.id.hierarquia_comercial_check_box);
        mNomeUsuarioTextView = view.findViewById(R.id.usuario_nome_text_view);
        mEmailUsuarioTextView = view.findViewById(R.id.usuario_email_text_view);
        mArrowImageView = view.findViewById(R.id.arrow_image_view);

        mSelecaoCheckBox.setChecked(node.isSelected());

        String nome = usuario.getNomeReduzido();

        mNomeUsuarioTextView.setText(TextUtils.isEmpty(usuario.getNome()) ?
                context.getString(R.string.help_desk_label) : nome);
        mEmailUsuarioTextView.setText(usuario.getNomeFuncao());
        mArrowImageView.setImageResource(node.isExpanded() ? R.drawable.ic_arrow_bottom :
                R.drawable.ic_arrow_right);

        mSelecaoCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            node.setSelected(isChecked);
            setChildrenAsSelected(node, isChecked);
        });

        mEmailUsuarioTextView.setVisibility(TextUtils.isEmpty(usuario.getNomeFuncao())
                ? View.GONE : View.VISIBLE);
        mArrowImageView.setVisibility(node.isLeaf() ? View.INVISIBLE : View.VISIBLE);

        return view;
    }

    private View createNodeViewForPage(TreeNode node, TreeNodePage page) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_hierarquia_comercial_pagina, null,
                false);

        mPaginaTextView = view.findViewById(R.id.pagina_text_view);
        mArrowImageView = view.findViewById(R.id.arrow_image_view);

        mPaginaTextView.setText(page.getLabel());

        mArrowImageView.setImageResource(node.isExpanded() ? R.drawable.ic_arrow_bottom :
                R.drawable.ic_arrow_right);

        return view;
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        if (mType.equals(Usuario.class)) {
            mSelecaoCheckBox.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
            mSelecaoCheckBox.setChecked(mNode.isSelected());
        }
    }

    @Override
    public void toggle(boolean active) {
        mArrowImageView.setImageResource(active ? R.drawable.ic_arrow_bottom :
                R.drawable.ic_arrow_right);
    }

    private void setChildrenAsSelected(TreeNode parentTreeNode, boolean isSelected) {
        for (TreeNode childTreeNode : parentTreeNode.getChildren()) {
            if (childTreeNode.getValue().getClass().equals(Usuario.class)) {
                getTreeView().selectNode(childTreeNode, isSelected);
            }
            setChildrenAsSelected(childTreeNode, isSelected);
        }
    }
}
