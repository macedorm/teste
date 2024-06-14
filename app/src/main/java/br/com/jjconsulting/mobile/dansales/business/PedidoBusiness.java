package br.com.jjconsulting.mobile.dansales.business;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.AgendaDao;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.ItemPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.PrecoVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.model.DateCompare;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

/**
 * Responsible for bussiness logic and operations about pedidos.
 */
public class PedidoBusiness {

    private static final String PEDIDO_CODIGO_IDENTITY_LETTER = "M";

    private OnAsyncResponse onAsyncResponseValidation;

    public PedidoBusiness() {
    }

    public boolean isAgenda(Usuario usuario, UnidadeNegocio unidadeNegocio, Cliente cliente) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                unidadeNegocio.getCodigo());
        boolean empresaComAgenda = "23".equals(empresaFilial[0]);
        boolean clienteComAgenda = cliente.getStatusCredito() == Cliente.STATUS_CREDITO_BLUE;
        boolean possuiAgenda = empresaComAgenda && clienteComAgenda;
        return usuario.getPerfil().permiteCriarPedidoAgenda(unidadeNegocio.getCodigo())
                && (cliente.isExclusivoPedidoAgenda() || possuiAgenda);
    }

    public boolean isAgendaOutrasOperacoes(Usuario usuario) {
        return usuario.getPerfil().getPerfisVenda().size() > 1;
    }

    public boolean isAgendaDistribuidor(AgendaDao agendaDao, Usuario usuario, Cliente cliente) {
        String codUnNeg = cliente.getCodigoUnidadeNegocio();
        boolean hasAgendaDistribuidor = agendaDao.hasAgendaDistribuidor(cliente.getCodigo());
        boolean hasTheRightTipoVenda = usuario.getPerfil().possuiTipoVenda(TipoVenda.VENDA, codUnNeg)
                || usuario.getPerfil().possuiTipoVenda(TipoVenda.VPR, codUnNeg);
        return hasAgendaDistribuidor && hasTheRightTipoVenda;
    }

    public boolean vizualizationMode(Pedido pedido, PedidoViewType pedidoViewType) {
        if (pedido == null)
            return false;

        if (pedido.getStatus().getCodigo() != StatusPedido.NAO_ENVIADO)
            return false;

        if (pedidoViewType != PedidoViewType.PEDIDO)
            return false;

        if (pedido.getOrigem().getCodigo() != OrigemPedido.MOBILE
                && !TipoVenda.SUG.equals(pedido.getCodigoTipoVenda()))
            return false;

        return true;
    }

    public void updatePedido(Context context, Pedido pedido, ArrayList<ItemPedido> itens, String codigoAntigoPedido) {
        PedidoDao pedidoDao = new PedidoDao(context);
        pedidoDao.update(pedido, codigoAntigoPedido);

        if (itens != null) {
            if (codigoAntigoPedido == null) {
                deleteAllItemPedido(context, pedido.getCodigo());
            } else {
                deleteAllItemPedido(context, codigoAntigoPedido);
            }

            insertItemPedido(context, itens, codigoAntigoPedido);
        }
    }

    public void deletePedido(Context context, Pedido pedido) {
        PedidoDao pedidoDao = new PedidoDao(context);
        pedidoDao.delete(pedido.getCodigo());

        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);
        itemPedidoDao.delete(pedido.getCodigo());
    }

    public void deleteAllItemPedido(Context context, String codigoPedido) {
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);
        itemPedidoDao.deleteAllItensPedido(codigoPedido);
    }

    public void insertItemPedido(Context context, ArrayList<ItemPedido> itens, String codigoAntigoPedido) {
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);

        for (ItemPedido itemPedido : itens) {
            if (codigoAntigoPedido != null) {
                itemPedido.setCodigoPedido(codigoAntigoPedido);
            }

            itemPedidoDao.insert(itemPedido);
        }
    }

    public void deleteAllItens(Context context, String codigoPedido) {
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);
        itemPedidoDao.deleteAllItensPedido(codigoPedido);

    }

    public void updateItemPedido(Context context, ArrayList<ItemPedido> itens) {
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);

        for (ItemPedido itemPedido : itens) {
            itemPedidoDao.update(itemPedido);
        }
    }

    public void updateCodPedItem(Context context, String codigoAntigoPedido, String novoCodigoPedido) {
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);
        itemPedidoDao.updateAllItemCodPed(codigoAntigoPedido, novoCodigoPedido);
    }

    /**
     * Creates a new pedido based on the current unidade de negócio,
     * usuário and cliente then persist it.
     */
    public Pedido createNewPedido(PedidoDao pedidoDao, UnidadeNegocio unidadeNegocio,
                                  Usuario usuario, Cliente cliente) {
        try{
            Pedido newPedido = createNewPedido(usuario, unidadeNegocio, cliente);
            pedidoDao.insert(newPedido);

            return pedidoDao.get(newPedido.getCodigo());

        } catch (Exception ex){
            return null;
        }
    }

    private Pedido createNewPedido(Usuario usuario, UnidadeNegocio unidadeNegocio,
                                   Cliente cliente) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                unidadeNegocio.getCodigo());

        Pedido pedido = new Pedido();
        pedido.setCodigo(createCodigoForNewPedido(usuario));
        pedido.setCodigoEmpresa(empresaFilial[0]);
        pedido.setCodigoFilial(empresaFilial[1]);
        pedido.setCodigoVendedor(usuario.getCodigo());
        pedido.setDataCadastro(new Date());
        pedido.setTipoAgenda(Pedido.TIPO_AGENDA_NAO_POSSUI);
        pedido.setCodigoCliente(cliente.getCodigo());
        pedido.setCodigoStatus(StatusPedido.NAO_ENVIADO);
        pedido.setCodigoOrigem(OrigemPedido.MOBILE);
        pedido.setCodigoPlanta(cliente.getPlanta());

        pedido.setCodigoCondPag(cliente.getCondicaoPagamento().getCodigo());


        pedido.setUrgente("N");

        String unMedidaPadrao = getUnMedidaPadrao(usuario, pedido.getCodigoUnidadeNegocio(),
                pedido.getCodigoTipoVenda(), cliente.getUnidadeMedidaPadrao());
        pedido.setUnidadeMedida(unMedidaPadrao);

        return pedido;
    }

    private String createCodigoForNewPedido(Usuario usuario) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        final String now = simpleDateFormat.format(new Date());
        final String userId = usuario.getCodigo();
        return String.format("%s%s%s", PEDIDO_CODIGO_IDENTITY_LETTER, now, userId);
    }

    /**
     * It doesn't persist the data, you must do it yourself.
     */
    public ItemPedido createNewItem(ItemPedidoDao itemPedidoDao, Usuario usuario, Pedido pedido) {
        int maxId = itemPedidoDao.getMaxId();
        return createItem(maxId, pedido);
    }

    /**
     * It doesn't persist the data, you must do it yourself.
     */
    public ItemPedido createNewItem(int maxId, Pedido pedido) {
        return createItem(maxId, pedido);
    }

    private ItemPedido createItem(int maxId, Pedido pedido){
        ItemPedido item = new ItemPedido();
        item.setId(maxId + 1);
        item.setCodigoPedido(pedido.getCodigo());
        item.setCodigoEmpresaProduto(pedido.getCodigoEmpresa());
        item.setCodigoFilialProduto(pedido.getCodigoFilial());

        return item;
    }

    /**
     * It builds the data. It considers that the given item already
     * has QUANTIDADE, DESCONTO and PRODUTO (because it normally comes from user input).
     */
    public ItemPedido buildNewItem(Pedido pedido, ItemPedido item) {

        if(item == null){
            return null;
        }

        Produto produto = item.getProduto();
        PrecoVenda preco = produto.getPrecoVenda();

        if (preco == null) {
            preco = new PrecoVenda();
            item.getProduto().setPrecoVenda(preco);
        }

        int quantidade = item.getQuantidade();
        int multiplo = produto.getMultiplo();

        double percentualDesconto = item.getPercentualDesconto();
        double precoVenda = preco.getPreco() - (preco.getPreco() * (percentualDesconto / 100));

        double precoTabela = preco.getPreco();
        double valorDesconto = preco.getPreco() - precoVenda; // unitário
        double total = quantidade * precoVenda;
        double peso = produto.getPeso();
        double pesoLiquido = produto.getPesoLiquido();

        if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(pedido.getUnidadeMedida())) {
            precoVenda *= multiplo;
            precoTabela *= multiplo;
            valorDesconto *= multiplo;
            total *= multiplo;
            peso *= multiplo;
            pesoLiquido *= multiplo;
        }

        if(quantidade == -1){
            precoVenda = 0;
            precoTabela = 0;
            valorDesconto = 0;
            total = 0;
            peso = 0;
            pesoLiquido = 0;

        }

        item.setPrecoVenda(precoVenda);
        item.setPrecoTabelaProduto(precoTabela);
        item.setPercentualDesconto(percentualDesconto);
        item.setDescontoPromocional(percentualDesconto);
        item.setValorDesconto(valorDesconto);
        item.setValorTotal(total);
        item.setPesoProduto(peso);
        item.setPesoLiquidoProduto(pesoLiquido);

        item.setObservacao("");
        item.setDescontoCliente(0);
        item.setDescontoFlagship(0);
        item.setDescontoGerencial(0);
        item.setDescontoCondicaoPagamento(0);
        item.setItSol("");
        item.setCodSol("");
        if(item.getLote() == null){
            item.setLote("");
        }

        return item;
    }


    public ItemPedido buildNewItemReb(Pedido pedido, ItemPedido item, int type) {
        Produto produto = item.getProduto();
        PrecoVenda preco = produto.getPrecoVenda();

        if (preco == null) {
            preco = new PrecoVenda();
            item.getProduto().setPrecoVenda(preco);
        }

        int quantidade = item.getQuantidade();


        double total =  item.getValorTotal();
        double gondula = item.getPrecoVenda();
        double peso = produto.getPeso();
        double pesoLiquido = produto.getPesoLiquido();


        if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(pedido.getUnidadeMedida())) {

            switch (type){
                case 0:
                    total = gondula;
                    gondula /= quantidade;
                    break;
                case 1:
                    gondula = produto.getMultiplo() * gondula;
                    total = gondula * quantidade;
                    break;
            }

            item.getProduto().setPrecoVenda(preco);
            peso *= item.getProduto().getMultiplo();
            pesoLiquido *= item.getProduto().getMultiplo();
        }else{
            peso *= quantidade;
            if(total == 0){
                total = gondula * quantidade;
            }
        }

        if(quantidade == -1){
            item.setPesoProduto(0);
            item.setPesoLiquidoProduto(0);
            item.setValorTotal(0);
        } else{
            item.setPesoProduto(peso);
            item.setPesoLiquidoProduto(pesoLiquido);
            item.setPrecoVenda(gondula);
            item.setValorTotal(total);
        }

        item.setObservacao("");
        item.setDescontoCliente(0);
        item.setDescontoFlagship(0);
        item.setDescontoGerencial(0);
        item.setDescontoCondicaoPagamento(0);
        item.setItSol("");
        item.setCodSol("");

        return item;
    }


    public double getPesoTotal(Pedido pedido, ArrayList<ItemPedido> itens) {
        if (itens == null || itens.size() == 0) {
            return 0;
        }

        double pesoTotal = 0;
        boolean ehCaixa = pedido.getUnidadeMedida().equals(Pedido.UNIDADE_MEDIDA_CAIXA);

        for (ItemPedido item : itens) { if(item.getQuantidade() > 0) {

                if (ehCaixa) {
                    pesoTotal += item.getQuantidade() *
                            (item.getProduto().getMultiplo() * item.getProduto().getPesoLiquido());
                } else {
                    pesoTotal += item.getQuantidade() * item.getProduto().getPesoLiquido();
                }
            }
        }

        return pesoTotal;
    }

    /**
     * Obtém o peso mínimo obrigatório de acordo com o perfil do usuário.
     */
    public double getPesoMinimo(Usuario usuario, Pedido pedido) {
        if (usuario == null)
            throw new IllegalArgumentException("usuario");

        if (pedido == null)
            throw new IllegalArgumentException("pedido");

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
        if (perfilVenda.getTipoPeso() == PerfilVenda.TIPO_PESO_ESPECIFICO)
            return perfilVenda.getPesoMinimo();
        else if (perfilVenda.getTipoPeso() == PerfilVenda.TIPO_PESO_CLIENTE || perfilVenda.getTipoPeso() == PerfilVenda.TIPO_WARNING_PESO_CLIENTE)
            return pedido.getCliente().getPesoMin();
        return 0;
    }

    /**
     * Obtém o peso mínimo obrigatório de acordo com o perfil do usuário.
     */
    public double getPesoMaximo(Usuario usuario, Pedido pedido) {
        if (usuario == null)
            throw new IllegalArgumentException("usuario");

        if (pedido == null)
            throw new IllegalArgumentException("pedido");

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
        if (perfilVenda.getTipoPesoMax() == PerfilVenda.TIPO_PESO_ESPECIFICO)
            return perfilVenda.getPesoMaximo();
        else if (perfilVenda.getTipoPesoMax() == PerfilVenda.TIPO_PESO_CLIENTE || perfilVenda.getTipoPeso() == PerfilVenda.TIPO_WARNING_PESO_CLIENTE)
            return 999999999;
        return 0;
    }

    /**
     * Obtém o valor mínimo obrigatório de acordo com o perfil do usuário.
     */
    public double getValorMinimo(Usuario usuario, Pedido pedido) {
        if (usuario == null)
            throw new IllegalArgumentException("usuario");

        if (pedido == null)
            throw new IllegalArgumentException("pedido");

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
        if (perfilVenda.getTipoValorMinimo() == PerfilVenda.TIPO_VALOR_MINIMO_VALIDA_PERFIL)
            return perfilVenda.getValorMinimo();
        else if (perfilVenda.getTipoPeso() == PerfilVenda.TIPO_VALOR_MINIMO_VALIDA_CADASTRO_CLIENTE)
            return pedido.getCliente().getValMin();

        return 0;
    }

    public String getUnMedidaPadrao(Usuario usuario, Pedido pedido) {
        return getUnMedidaPadrao(usuario, pedido.getCodigoUnidadeNegocio(),
                pedido.getCodigoTipoVenda(), pedido.getCliente().getUnidadeMedidaPadrao());
    }

    public String getUnMedidaPadrao(Usuario usuario, String unidadeNegocio, String tipoVenda,
                                    String unidadeMedidaCliente) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(tipoVenda, unidadeNegocio);

        switch (perfilVenda.getUnMedidaPadrao()) {
            case PerfilVenda.UN_MEDIDA_INDISPONIVEL:
                return Pedido.UNIDADE_MEDIDA_CAIXA;
            case PerfilVenda.UN_MEDIDA_CAIXA:
                return Pedido.UNIDADE_MEDIDA_UNIDADE;
            case PerfilVenda.UN_MEDIDA_PADRAO_CLIENTE:
                return TextUtils.isEmpty(unidadeMedidaCliente) ? null : unidadeMedidaCliente;
            default:
                return Pedido.UNIDADE_MEDIDA_CAIXA;
        }
    }

    public void executeValidadePedido(Context context, Pedido pedido, ArrayList<ItemPedido> itens, OnAsyncResponse onAsyncResponseValidation) {
        this.onAsyncResponseValidation = onAsyncResponseValidation;
        AsyncTaskValidationPedido asyncTaskValidationPedido = new AsyncTaskValidationPedido(context, pedido, itens);
        asyncTaskValidationPedido.execute();
    }

    public ValidationDan validatePedido(Context context, Pedido pedido, ArrayList<ItemPedido> itens) {
        ValidationDan validation = new ValidationDan();

        if (pedido.getCodigoTipoVenda() == null) {
            validation.addError(context.getString(R.string.message_error_tipo_venda));
        } else {
            if (TipoVenda.BON.equals(pedido.getCodigoTipoVenda())) {
                validation.addValidation(validatePedidoBonificado(context, pedido, itens));
            }

            Usuario usuario = Current.getInstance(context).getUsuario();
            PerfilVenda perfil = usuario.getPerfil().getPerfilVenda(pedido);
            if ((pedido.getObservacaoNotaFiscal() == null
                    || pedido.getObservacaoNotaFiscal().trim().length() == 0)
                    && perfil.getTipoObsNf() == PerfilVenda.TIPO_OBS_NF_EXBIGIR_OBRIGATORIO) {

                if (perfil.getObsNfInfo() != null && perfil.getObsNfInfo().trim().length() > 0) {
                    validation.addError(context.getString(R.string.message_error_obs_nf) + "\n" + perfil.getObsNfInfo());
                } else {
                    validation.addError(context.getString(R.string.message_error_obs_nf));
                }

            }

            if (pedido.getObservacao() == null || pedido.getObservacao().trim().length() == 0) {
                if (perfil.getTipoObsInterna() == PerfilVenda.TIPO_OBS_INTERNA_EXBIGIR_OBRIGATORIO) {
                    validation.addError(context.getString(R.string.message_error_obs_internal));
                } else if (perfil.getTipoObsInterna() == PerfilVenda.TIPO_OBS_INTERNA_EXIBIR_OBRIGATORIO_URGENTE
                        && pedido.getUrgente().equals("S")) {
                    if (perfil.getObsInternaInfo() != null && perfil.getObsInternaInfo().trim().length() > 0) {
                        validation.addError(context.getString(R.string.message_error_obs_internal_urg)
                                + "\n" + perfil.getObsInternaInfo());
                    } else {
                        validation.addError(context.getString(R.string.message_error_obs_internal_urg));
                    }
                }
            }


            if (perfil.isEdicaoDataEntregaHabilitada()) {
                DateCompare firstCond = FormatUtils.compareDate(pedido.getEntregaEm(), Calendar.getInstance().getTime());
                DateCompare secondCond = FormatUtils.compareDate(pedido.getEntregaEm(), FormatUtils.getDateTimeNow(0, 3, 0));
                if (firstCond != DateCompare.AFTER ) {
                    validation.addError(context.getString(R.string.message_error_date_error));
                }
            }

            // validação de peso mínimo
            if (perfil.getTipoPeso() != PerfilVenda.TIPO_PESO_NAO_VALIDAR) {
                double pesoMinimo = getPesoMinimo(usuario, pedido);
                double pesoTotal = getPesoTotal(pedido, itens);
                if (pesoMinimo > pesoTotal) {
                    if (perfil.getTipoPeso() == PerfilVenda.TIPO_WARNING_PESO_CLIENTE) {
                        validation.addWarning(context.getString(R.string.message_error_peso_minimo,
                                String.format("(%s)", context.getString(R.string.unidade_peso_kg,
                                        String.valueOf(pesoMinimo)))));
                    } else {
                        validation.addError(context.getString(R.string.message_error_peso_minimo,
                                String.format("(%s)", context.getString(R.string.unidade_peso_kg,
                                        String.valueOf(pesoMinimo)))));
                    }
                }

            }

            // validação de peso máximo
            if (perfil.getTipoPesoMax() != PerfilVenda.TIPO_PESO_NAO_VALIDAR) {
                double pesoMaximo = getPesoMaximo(usuario, pedido);
                double pesoTotal = getPesoTotal(pedido, itens);

                if (pesoMaximo > 0 && pesoMaximo < pesoTotal) {
                    validation.addError(context.getString(R.string.message_error_peso_maximo,
                            String.format("(%s)", context.getString(R.string.unidade_peso_kg,
                                    String.valueOf(pesoMaximo)))));
                }
            }


            double valorMinimo = getValorMinimo(usuario, pedido);
            if (valorMinimo < 0) {
                int percentValorMinimo = (int) ((pedido.getValorTotal() * 100) / valorMinimo);
                if (percentValorMinimo != 100) {
                    validation.addError(context.getString(R.string.message_error_valor_minimo, String.format("%s (%s)", FormatUtils.toBrazilianRealCurrency(valorMinimo), percentValorMinimo + "%")));
                }
            }
        }

        if (pedido.getObservacao() != null && pedido.getObservacao().trim().length() > 70) {
            validation.addError(context.getString(R.string.message_error_obs_internal_max));
        }


        boolean isItens = false;

        for(ItemPedido itemPedido: itens){
            if(itemPedido.getQuantidade() > 0){
                isItens = true;
                break;
            }
        }

        if (!isItens) {
            validation.addError(context.getString(R.string.message_error_sem_item));
        }

        if (pedido.getUrgente().toLowerCase().contains("s")) {
            validation.addWarning(context.getString(R.string.message_warnign_pedido_urgente));
        }

        if (validation.isValid()) {
            validation.addInfo(context.getString(R.string.message_info_pedido));
        }

        return validation;
    }

    private ValidationDan validatePedidoBonificado(Context context, Pedido pedido, ArrayList<ItemPedido> itens) {

        ValidationDan validation = new ValidationDan();

        if (!TipoVenda.BON.equals(pedido.getCodigoTipoVenda())) {
            return validation;
        }

        ClienteDao clienteDao = new ClienteDao(context);
        if (!clienteDao.hasCampanha(pedido.getCodigoCliente())) {
            validation.addError(context.getString(R.string.message_error_bon_campanha));
        }

        String codPedVenda = pedido.getCodigoPedidoVenda();
        if (codPedVenda == null || TextUtils.isEmpty(codPedVenda)) {
            validation.addError(context.getString(R.string.message_error_bon_codped));
            return validation;
        }

        Pedido pedidoPai = new PedidoDao(context).get(codPedVenda);
        if (pedidoPai == null) {
            validation.addError(context.getString(R.string.message_error_bon_naolocalizado));
            return validation;
        } else {

            if (!DateUtils.isToday(pedidoPai.getDataCadastro().getTime())) {
                validation.addError(context.getString(R.string.message_error_bon_data));
            }
        }

        return validation;
    }

    public ValidationDan validateItem(Context context, ItemPedidoDao itemPedidoDao, ProdutoDao produtoDao,
                                      Usuario usuario, Pedido pedido, ItemPedido item,
                                      boolean isNew) {
        ValidationDan validation = new ValidationDan(item);

        if (item == null || item.getProduto() == null) {
            validation.addError("Item inválido");
            return validation;
        }

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
        Produto produto = item.getProduto();
        PrecoVenda precoVenda = produto.getPrecoVenda();
        String unidadeNegocio = pedido.getCodigoEmpresa() + pedido.getCodigoFilial();
        boolean ehDAT = TipoVenda.DAT.equals(pedido.getCodigoTipoVenda());
        boolean isReb = TipoVenda.REB.equals(pedido.getCodigoTipoVenda());

        if (item.getQuantidade() <= 0) {
            validation.addError(context.getString(R.string.message_error_qtd_invalid));
        }

        // validação: desconto negativo
        if (item.getPercentualDesconto() < 0) {
            validation.addError(context.getString(R.string.message_error_desc_invalid));
        }

        // validação: permissão de desconto
        if ((PerfilVenda.TIPO_DESCONTO_SEM_PERMISSAO == perfilVenda.getTipoDesconto()
                && !ehDAT)
                && item.getPercentualDesconto() > 0) {
            validation.addError(context.getString(R.string.message_error_no_desc_invalid));
        }

        // validação: desconto máximo
        if (ehDAT && item.getPercentualDesconto() > precoVenda.getDescontoMaximo()) {
            double descontoMaximo = precoVenda.getDescontoMaximo()
                    + produtoDao.getDescontoDAT(unidadeNegocio, produto.getCodigo(),
                    pedido.getCodigoPlanta(), item.getLote());
            if (item.getPercentualDesconto() > descontoMaximo) {
                validation.addError(context.getString(R.string.message_error_max_desc_invalid));
            }
        } else if (PerfilVenda.TIPO_DESCONTO_PADRAO == perfilVenda.getTipoDesconto()
                && item.getPercentualDesconto() > precoVenda.getDescontoMaximo()) {
            validation.addError(context.getString(R.string.message_error_max_desc_invalid));
        }

        // validação: preço de venda maior que preço tabela
        if (!perfilVenda.isValidacaoProdutoBloqueadoIgnorada()) {
            double precoTabela = Pedido.UNIDADE_MEDIDA_UNIDADE.equals(pedido.getUnidadeMedida()) ?
                    precoVenda.getPreco() : precoVenda.getPreco() * produto.getMultiplo();
            if (item.getPrecoVenda() > precoTabela) {
                validation.addError(context.getString(R.string.message_error_price_major_table));
            }
        }

        // validação: uso do múltiplo
        if (perfilVenda.isDigitacaoMultiploObrigatoria()
                && Pedido.UNIDADE_MEDIDA_UNIDADE.equals(pedido.getUnidadeMedida())
                && !ehDAT
                && produto.getMultiplo() > 0
                && (item.getQuantidade() % produto.getMultiplo() != 0)) {

            if(!perfilVenda.isPerCaixaFrac() ||
                    !produtoDao.hasCaixaFracionada(unidadeNegocio, produto.getCodigo())){
                validation.addError(String.format(context.getString(R.string.message_error_qtd_no_mult) + " %d",
                        produto.getMultiplo()));
            }


        }

        if(isReb){
            if(item.getValorTotal() == 0){
                validation.addError(String.format(context.getString(R.string.message_error_value_reb)));
            }

            if(br.com.jjconsulting.mobile.jjlib.util.TextUtils.isNullOrEmpty(item.getLote())){
                validation.addError(String.format(context.getString(R.string.message_error_expiration_data)));
            }
        }

        if (ehDAT) {
            // lote
            if (TextUtils.isEmpty(item.getLote())) {
                validation.addError(context.getString(R.string.message_error_prod_invalid_lot));
            } else {
                // saldo
                int saldo = produtoDao.getSaldoDAT(unidadeNegocio, produto.getCodigo(),
                        pedido.getCodigoPlanta(), item.getLote(), pedido.getUnidadeMedida());
                if (item.getQuantidade() > saldo) {
                    validation.addError(
                            String.format(context.getString(R.string.message_error_qtd_no_balance)
                                    + " %d", saldo));
                }

                // produto duplicado
                if (itemPedidoDao.isProdutoWithLoteAlreadyInThePedido(pedido.getCodigo(),
                        produto.getCodigo(), item.getLote())) {
                    validation.addError(context.getString(R.string.message_error_prod_lot));
                }
            }
        } else {
            if (isNew) {
                if(isReb){
                    if (itemPedidoDao.isProdutoAlreadyInThePedido(pedido.getCodigo(),
                            produto.getCodigo(), item.getLote())) {
                        validation.addError(context.getString(R.string.message_error_prod_experation_date));
                    }
                } else {
                    if (itemPedidoDao.isProdutoAlreadyInThePedido(pedido.getCodigo(),
                            produto.getCodigo())) {
                        validation.addError(context.getString(R.string.message_error_prod));
                    }
                }
            }
        }

        return validation;
    }

    public boolean checkSubstituto(ArrayList<ItemPedido> itensAdded, ArrayList<ItensSortimento> itens, String codItensSubstituto ){

        boolean isChecked = true;

        for (ItensSortimento item:itens) {
            if(codItensSubstituto.equals(item.getSKU())){
                isChecked = false;
            }
        }

        if(isChecked){
            for(ItemPedido itemPedido: itensAdded){
                if(itemPedido.getProduto().getCodigo().equals(codItensSubstituto)){
                    isChecked = false;
                    break;
                }
            }
        }


        return isChecked;

    }

    public String getCodSubstituto(String SKU, ArrayList<ItensSortimento> itens){
        String id = "";

        for(ItensSortimento item:itens){
            if(item.getSKU().equals(SKU)){
                if(!br.com.jjconsulting.mobile.jjlib.util.TextUtils.isNullOrEmpty(item.getSUBSTITUTE())){
                    id = item.getSUBSTITUTE();
                }
                break;
            }
        }

        return id;
    }

    public int getItensSubstitutoAdd(ArrayList<ItemPedido> itens){
        int qtdSub = 0;

        for(ItemPedido item:itens){
            if(item.getQuantidade() > 0 && item.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_SUBSTITUTO){
                qtdSub++;
            }
        }

        return qtdSub;
    }

    public ArrayList<ItemPedido> removeItensNegativo(ArrayList<ItemPedido> itens){
        ArrayList<ItemPedido> itensSend = new ArrayList<>();

        for(ItemPedido item:itens){
            if(item.getQuantidade() > 0){
                itensSend.add(item);
            }
        }

        return itensSend;
    }

    public  ArrayList<ItemPedido> checkItens( ArrayList<ItemPedido> itensPedido, ArrayList<ItensSortimento> itensSortimento){
        ArrayList<ItemPedido> itensObr = new ArrayList<>();
        PedidoBusiness pedidoBusiness = new PedidoBusiness();

        for (ItemPedido itemPedido : itensPedido) {

            if(itemPedido.getProduto().getTipoSortimento() != null && itemPedido.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_SUBSTITUTO){
                itemPedido.getProduto().setTipoSortimento(null);
            }

            if (itemPedido.getProduto().getTipoSortimento() != null) {
                switch (itemPedido.getProduto().getTipoSortimento()){
                    case Produto.SORTIMENTO_TIPO_OBRIGATORIO:
                        if(itemPedido.getQuantidade() < 1){
                            if(itemPedido.getCodSubstituto() != 0){
                                for (ItemPedido itemPedidoSub : itensPedido) {
                                    if(itemPedidoSub.getId() == itemPedido.getCodSubstituto()){
                                        if(itemPedidoSub.getQuantidade() < 1){
                                            itensObr.add(itemPedido);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                String codSub = pedidoBusiness.getCodSubstituto(itemPedido.getProduto().getCodigo(), itensSortimento);

                                if(!br.com.jjconsulting.mobile.jjlib.util.TextUtils.isNullOrEmpty(codSub)){
                                    for(ItemPedido itemSub : itensPedido){
                                        if(itemSub.getProduto().getCodigo().equals(codSub)){
                                            if(itemSub.getQuantidade() < 1){
                                                itensObr.add(itemPedido);
                                            }
                                        }
                                    }
                                } else {
                                    itensObr.add(itemPedido);
                                }
                            }
                        }
                        break;
                }
            }
        }

        return itensObr;
    }

    public class AsyncTaskValidationPedido extends AsyncTask<Void, Void, ValidationDan> {

        private Context context;
        private Pedido pedido;
        private ArrayList<ItemPedido> itens;

        public AsyncTaskValidationPedido(Context context, Pedido pedido, ArrayList<ItemPedido> itens) {
            this.context = context;
            this.pedido = pedido;
            this.itens = itens;
        }

        @Override
        protected ValidationDan doInBackground(Void... params) {
            return validatePedido(context, pedido, itens);
        }

        @Override
        protected void onPostExecute(ValidationDan objects) {
            onAsyncResponseValidation.processFinish(objects);
        }
    }

    public interface OnAsyncResponse {
        void processFinish(ValidationDan objects);
    }
}
