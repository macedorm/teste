# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/rodrigomacedo/Documents/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dontoptimize
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes Signature
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

-keep class com.google.gson.** { *; }
-keep class org.apache.** { *; }
-keep class com.google.android.** { *; }
-keep class com.google.android.maps.** { *; }
-dontwarn com.google.android.maps.**

-keep class android.view.accessibility.** { *; }
-keep class com.google.android.gms.** { *; }
 -dontwarn com.google.android.gms.**


-keepattributes InnerClasses
 -keep class br.com.jjconsulting.mobile.dansales.model.Agenda**
 -keep class br.com.jjconsulting.mobile.dansales.model.Bandeira**
 -keep class br.com.jjconsulting.mobile.dansales.model.BatchDAT**
 -keep class br.com.jjconsulting.mobile.dansales.model.PesquisaResposta**
 -keep class br.com.jjconsulting.mobile.dansales.model.Cliente**
 -keep class br.com.jjconsulting.mobile.dansales.model.CondicaoPagamento**
 -keep class br.com.jjconsulting.mobile.dansales.model.CondicaoPerguntaPesquisa**
 -keep class br.com.jjconsulting.mobile.dansales.model.EstoqueDAT**
 -keep class br.com.jjconsulting.mobile.dansales.model.Familia**
 -keep class br.com.jjconsulting.mobile.dansales.model.Grupo**
 -keep class br.com.jjconsulting.mobile.dansales.model.ItemPedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.Login**
 -keep class br.com.jjconsulting.mobile.dansales.model.LogPedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.MasterData**
 -keep class br.com.jjconsulting.mobile.dansales.model.MasterDataSync**
 -keep class br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaOpcoes**
 -keep class br.com.jjconsulting.mobile.dansales.model.Organizacao**
 -keep class br.com.jjconsulting.mobile.dansales.model.OrigemPedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.Pedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.PedidoTabType**
 -keep class br.com.jjconsulting.mobile.dansales.model.PedidoViewType**
 -keep class br.com.jjconsulting.mobile.dansales.model.Perfil**
 -keep class br.com.jjconsulting.mobile.dansales.model.PerfilVenda**
 -keep class br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta**
 -keep class br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaType**
 -keep class br.com.jjconsulting.mobile.dansales.model.Pesquisa**
 -keep class br.com.jjconsulting.mobile.dansales.model.Planta**
 -keep class br.com.jjconsulting.mobile.dansales.model.PrecoVenda**
 -keep class br.com.jjconsulting.mobile.dansales.model.Produto**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiroPedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoDetail**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoStatus**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioNotas**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem**
 -keep class br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo**
 -keep class br.com.jjconsulting.mobile.dansales.model.PesquisaResposta**
 -keep class br.com.jjconsulting.mobile.dansales.model.StatusPedido**
 -keep class br.com.jjconsulting.mobile.dansales.model.SyncInfo**
 -keep class br.com.jjconsulting.mobile.dansales.model.SyncPedidoProcessDetail**
 -keep class br.com.jjconsulting.mobile.dansales.model.TipoAmbiente**
 -keep class br.com.jjconsulting.mobile.dansales.model.TipoVenda**
 -keep class br.com.jjconsulting.mobile.dansales.model.TreeNodePage**
 -keep class br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration**
 -keep class br.com.jjconsulting.mobile.dansales.model.UnidadeMedida**
 -keep class br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio**
 -keep class br.com.jjconsulting.mobile.dansales.model.Usuario**
 -keep class br.com.jjconsulting.mobile.dansales.model.UsuarioFuncao**
 -keep class br.com.jjconsulting.mobile.dansales.kotlin.model.bSituacaoNotaFiscalNotasCarga**


-keep public class org.jsoup.** {
public *;
}

-keepclassmembers class br.com.jjconsulting.mobile.dansales.model** { *;}

-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
