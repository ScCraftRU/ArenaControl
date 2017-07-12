package ru.sccraft.arenacontrol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.JsonSyntaxException;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ServerInfoWidgetConfigureActivity ServerInfoWidgetConfigureActivity}
 */
public class ServerInfoWidget extends AppWidgetProvider {

    private static final String ACTION_WIDGET_RECEIVER = "ru.sccraft.arenacontrol.widgetclicked";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        CharSequence widgetText = ServerInfoWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.server_info_widget);

        try {
            String файл = widgetText + ".json";
            Fe fe = new Fe(context);
            Server сервер = Server.fromJSON(fe.getFile(файл));
            views.setTextViewText(R.id.widgetInfo_serverName, сервер.имя_сервера + " (" + сервер.id + ")\nIP: " + сервер.ip);
            views.setTextViewText(R.id.appwidget_text, widgetText);
            Intent intent = new Intent(context, ServerActivity.class);
            //intent.setAction(ACTION_WIDGET_RECEIVER);
            intent.putExtra("server", сервер.toJSON());
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetInfo_serverName, actionPendingIntent);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            ServerInfoWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (ACTION_WIDGET_RECEIVER.equals(action)) {
            Log.d("InfoWitget", "Произошло нажатие на виджет");
            context.startActivity(intent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}