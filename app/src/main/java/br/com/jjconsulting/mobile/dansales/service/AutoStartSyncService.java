package br.com.jjconsulting.mobile.dansales.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AutoStartSyncService  extends BroadcastReceiver {

    private static final long REFRESH_INTERVAL = 3600 * 1000;
    private static final int JOB_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleAutoSync(context, true);
    }

    public static void scheduleAutoSync(Context context, boolean isCancel){

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            long interval = REFRESH_INTERVAL;

            try {
                UserInfo userInfo = new UserInfo();
                Login login = userInfo.getUserInfo(context);

                //Recupera do perfil intervalo para auto sincronismo
                if (login != null) {

                    UsuarioDao usuarioDao = new UsuarioDao(context);
                    Usuario usuario = usuarioDao.get(login.getUserId());

                    if (usuario != null) {
                        Perfil perfil = usuario.getPerfil();

                        if (perfil != null) {

                            if (perfil.getIntervaloAutoSync() > 0) {
                                interval = perfil.getIntervaloAutoSync();
                            }

                            LogUser.log("AUTO-SYNC: isPermiteAutoSync - " + perfil.isPermiteAutoSync());

                        }
                    }
                }
            }catch (Exception ex){
                //Nao foi possivel recuperar os dados do usuário mantem agendamento em 1hora
                LogUser.log(ex.toString());
            }

            LogUser.log("AUTO-SYNC: Intervalo - " + interval);

            //Realiza o agendamento do auto sincronismo
            ComponentName serviceComponent = new ComponentName(context, SyncManagerService.class);
            JobInfo jobInfo;

            jobInfo = new JobInfo.Builder(JOB_ID, serviceComponent)
                    .setMinimumLatency(interval).build();

            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

            if (isCancel) {
                jobScheduler.cancelAll();
            }

            if (jobScheduler.getAllPendingJobs().size() == 0) {
                jobScheduler.schedule(jobInfo);
                LogUser.log("AUTO-SYNC: Agendado - ok");
            } else {
                LogUser.log("AUTO-SYNC: Agendado - error - fila " + jobScheduler.getAllPendingJobs().size());
            }
        }
    }
}
