package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.LayoutDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaPerguntaDao;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaType;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;

public class AsyncTaskPerguntas extends AsyncTask<Void, Void, List<PesquisaPergunta>> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private PesquisaPerguntaDao mPesquisaPerguntaDao;
    private LayoutDao mLayoutDao;
    private TFreq tFreq;
    private Layout mLayout;

    private int mCodigoPesquisa;

    private String mCodigoCliente;
    private String mUnidadeNegocio;

    public AsyncTaskPerguntas(Context context, int codigoPesquisa, String codigoCliente, Layout layout, TFreq tFreq, Date currentDate, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mCodigoPesquisa = codigoPesquisa;
        this.mCodigoCliente = codigoCliente;
        this.tFreq = tFreq;
        this.mLayout = layout;

        this.mPesquisaPerguntaDao = new PesquisaPerguntaDao(context, currentDate);
        this.mLayoutDao = new LayoutDao(context);

        mUnidadeNegocio = Current.getInstance(context).getUnidadeNegocio().getCodigo();
    }

    @Override
    protected List<PesquisaPergunta> doInBackground(Void... params) {
        List<PesquisaPergunta> mPerguntas = new ArrayList<>();
        List<PesquisaPergunta>  perguntasTemp = mPesquisaPerguntaDao.getPerguntas(mCodigoPesquisa, Current.getInstance(context).getUsuario().getCodigo(), mCodigoCliente, tFreq);

        if(mLayout == null){
            mLayout = new Layout();
            mLayout = mLayoutDao.getLayout(mUnidadeNegocio,
                    mCodigoCliente, new Date());
        }

        ArrayList<ItensListSortimento> itensListSortimento;

        for(PesquisaPergunta pesquisaPergunta:perguntasTemp) {
            boolean add = false;

            if(pesquisaPergunta.getTipo().getValue() <  12 || pesquisaPergunta.getTipo().getValue() > 15) {
                add = true;
            } else if(pesquisaPergunta.getTipo() == PesquisaPerguntaType.SORTIMENTO_OBRIGATORIO_PRECO){
                itensListSortimento = getSortimento(mCodigoCliente, TSortimento.OBRIGATORIO, true);

                if(itensListSortimento != null && itensListSortimento.size() > 0){
                    pesquisaPergunta.setItensListSortimento(itensListSortimento);
                    add = true;
                }
            } else {
                TSortimento tSortimento = TSortimento.OBRIGATORIO;

                switch (pesquisaPergunta.getTipo()) {
                    case SORTIMENTO_RECOMENDADO:
                        tSortimento = TSortimento.RECOMENDADO;
                        break;
                    case SORTIMENTO_INOVACAO:
                        tSortimento = TSortimento.INOVACAO;
                        break;
                }

                itensListSortimento = getSortimento(mCodigoCliente, tSortimento, false);

                if(itensListSortimento != null && itensListSortimento.size() > 0){
                    pesquisaPergunta.setItensListSortimento(itensListSortimento);
                    add = true;
                }
            }

            if(add){
                mPerguntas.add(pesquisaPergunta);
            }
        }

        return mPerguntas;
    }

    protected ArrayList<ItensListSortimento> getSortimento(String codCli, TSortimento tSortimento , boolean isPreco) {
        SortimentoDao sortimentoDao = new SortimentoDao(context);

        ClienteDao clienteDao = new ClienteDao(context);
        Cliente cliente = clienteDao.get(mUnidadeNegocio, codCli);

        ArrayList<ItensListSortimento> itens = sortimentoDao.getItensSortimentoChecklist(mLayout != null ? mLayout.getCodigo(): "", tSortimento.getValue(), new Date(), mUnidadeNegocio, isPreco);

        if(isPreco && itens != null){
            for(int ind = 0; ind < itens.size(); ind++){
                String variante = itens.get(ind).getVariante();
                itens.set(ind, sortimentoDao.getPrecoSortimento(itens.get(ind),  cliente.getCodCanal(), variante, mUnidadeNegocio));
            }
        }

        return itens;
    }

    @Override
    protected void onPostExecute(List<PesquisaPergunta> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<PesquisaPergunta> objects);
    }
}
