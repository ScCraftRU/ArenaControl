package ru.sccraft.arenacontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

public class ServerActivity extends ADsActivity {

    Server сервер;
    AdView adView;
    private boolean обновлён = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Объект {@link ViewPager} отвечает за вкладки.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        } else {
            сервер = Server.fromJSON(savedInstanceState.getString("server"));
            обновлён = savedInstanceState.getBoolean("server_updated");
        }
        setContentView(R.layout.activity_server);

        adView = findViewById(R.id.adView);
        if (сервер.id != 0){
            задать_баннер(adView);
        } else {
            adView.setVisibility(View.GONE);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (сервер.id == 0) {
                    Snackbar.make(view, R.string.serverActivity_error, Snackbar.LENGTH_LONG).setAction("Remove server", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            удалить();
                        }
                    }).show();
                    return;
                }
                Intent intent = new Intent(ServerActivity.this, ConsoleActivity.class);
                intent.putExtra("server", сервер.toJSON());
                startActivity(intent);
            }
        });

        setTitle(сервер.имя_сервера + " (" + сервер.id + ")");
        if (!обновлён) {
            обновить();
            обновлён = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_update:
                обновить();
                return true;
            case R.id.action_edit_commands:
                Intent intent = new Intent(ServerActivity.this, CommandEditActivity.class);
                intent.putExtra("server", сервер.toJSON());
                startActivity(intent);
                return true;
            case R.id.action_clear:
                сервер.очистить_комманды();
                fe.saveFile(сервер.получить_токен() + ".json", сервер.toJSON());
                finish();
                return true;
            case R.id.action_remove:
                удалить();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ServerInfoFragment extends Fragment {
        private View rootView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ServerInfoFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ServerInfoFragment newInstance(int sectionNumber) {
            ServerInfoFragment fragment = new ServerInfoFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_server, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView;
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            TextView name = (TextView) rootView.findViewById(R.id.server_name);
            ServerActivity s = (ServerActivity) getActivity();
            name.setText(s.сервер.имя_сервера);
            TextView id = (TextView) rootView.findViewById(R.id.server_id);
            id.setText("" + s.сервер.id);
            TextView status = (TextView) rootView.findViewById(R.id.serverActivity_status);
            status.setText(rootView.getResources().getStringArray(R.array.statusArray)[s.сервер.статус]);
            TextView IP = (TextView) rootView.findViewById(R.id.server_ip);
            IP.setText(s.сервер.ip);
            TextView игра = (TextView) rootView.findViewById(R.id.server_game);
            игра.setText(s.сервер.игра);
            TextView игра_версия = (TextView) rootView.findViewById(R.id.server_version);
            игра_версия.setText(s.сервер.игра_версия);
            TextView плагины = (TextView) rootView.findViewById(R.id.server_plugins);
            плагины.setText(s.сервер.плагины);
            TextView дней_до_окончания_аренды = (TextView) rootView.findViewById(R.id.server_days);
            дней_до_окончания_аренды.setText("" + s.сервер.дней_до_окончания_аренды);
        }
    }

    public static class ServerPlayersFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private View rootView;
        private ServerActivity s;

        public ServerPlayersFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ServerPlayersFragment newInstance(int sectionNumber) {
            ServerPlayersFragment fragment = new ServerPlayersFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_players, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView;
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            ListView lv = (ListView) rootView.findViewById(R.id.listView_players);
            s = (ServerActivity) getActivity();
            String[] игроки = s.сервер.игроки;
            if (игроки == null) return;
            ArrayAdapter<String> aa = new ArrayAdapter<>(s, android.R.layout.simple_list_item_1, игроки);
            lv.setAdapter(aa);
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (s.сервер.игроки[position].equals("")) return true;
                    s.сервер.выполнить_комманду("kick " + s.сервер.игроки[position] + " " + getString(R.string.player_kickByApp));
                    return true;
                }
            });
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (s.сервер.игроки[position].equals("")) return;
                    Intent intent = new Intent(s, PlayerActivity.class);
                    intent.putExtra("server", s.сервер.toJSON());
                    intent.putExtra("name", s.сервер.игроки[position]);
                    startActivity(intent);
                }
            });
        }
    }

    public static class ServerResFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private View rootView;

        public ServerResFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ServerResFragment newInstance(int sectionNumber) {
            ServerResFragment fragment = new ServerResFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_resources, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView;
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            final ServerActivity s = (ServerActivity) getActivity();
            ProgressBar игроки = rootView.findViewById(R.id.res_players_pb);
            игроки.setMax(s.сервер.игроки_всего);
            игроки.setProgress(s.сервер.игроки_на_сервере);
            TextView игроки1 = rootView.findViewById(R.id.res_players_title);
            игроки1.setText(игроки1.getText().toString() + " (" + s.сервер.игроки_на_сервере + "/" + s.сервер.игроки_всего);
            ProgressBar процессор = rootView.findViewById(R.id.res_cpu_pb);
            процессор.setMax(100);
            процессор.setProgress(s.сервер.процессор_в_процентах);
            TextView процессор1 = rootView.findViewById(R.id.res_cpu_title);
            процессор1.setText(процессор1.getText().toString() + " (" + s.сервер.процессор_в_процентах + "%)");
            ProgressBar озу = (ProgressBar) rootView.findViewById(R.id.res_ram_pb);
            озу.setMax(s.сервер.озу_всего);
            озу.setProgress(s.сервер.озу_использовано);
            TextView озу1 = rootView.findViewById(R.id.res_ram_title);
            озу1.setText(озу1.getText().toString() + " (" + s.сервер.озу_в_процентах + "%)");
            ProgressBar диск = rootView.findViewById(R.id.res_disk_pb);
            диск.setMax(s.сервер.диск_всего);
            диск.setProgress(s.сервер.диск_использовано);
            TextView диск1 = rootView.findViewById(R.id.res_disk_title);
            диск1.setText(диск1.getText().toString() + " (" + s.сервер.диск_в_процентах + "%)");

            Button включить = rootView.findViewById(R.id.res_start);
            if (s.сервер.статус == 0) {
                включить.setVisibility(View.VISIBLE);
            } else {
                включить.setVisibility(View.GONE);
            }
            включить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.сервер.включить(new Server.Операция_завершена() {
                        @Override
                        public void успешно() {
                            s.обновить();
                        }

                        @Override
                        public void ошибка() {
                            Toast.makeText(rootView.getContext().getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    s.обновить();
                }
            });
            Button выключить = rootView.findViewById(R.id.res_stop);
            if (s.сервер.статус != 0) {
                выключить.setVisibility(View.VISIBLE);
            } else {
                выключить.setVisibility(View.GONE);
            }
            выключить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.сервер.выключить(new Server.Операция_завершена() {
                        @Override
                        public void успешно() {
                            s.обновить();
                        }

                        @Override
                        public void ошибка() {
                            Toast.makeText(rootView.getContext().getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            Button перезагрузить = rootView.findViewById(R.id.res_reboot);
            if (s.сервер.статус == 1) {
                перезагрузить.setVisibility(View.VISIBLE);
            } else {
                перезагрузить.setVisibility(View.GONE);
            }
            перезагрузить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.сервер.перезагрузить(new Server.Операция_завершена() {
                        @Override
                        public void успешно() {
                            s.обновить();
                        }

                        @Override
                        public void ошибка() {
                            Toast.makeText(rootView.getContext().getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            Button перезагрузить_плагины = rootView.findViewById(R.id.res_reload);
            if ((s.сервер.статус == 1) && (!s.сервер.плагины.equals(""))) {
                перезагрузить_плагины.setVisibility(View.VISIBLE);
            } else {
                перезагрузить_плагины.setVisibility(View.GONE);
            }
            перезагрузить_плагины.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s.сервер.выполнить_комманду("reload");
                }
            });
        }
    }

    public static class ServerWorldFragment extends Fragment {
        private View rootView;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ServerWorldFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ServerWorldFragment newInstance(int sectionNumber) {
            ServerWorldFragment fragment = new ServerWorldFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_world, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView;
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            final ServerActivity s = (ServerActivity) getActivity();
            Button день = (Button) rootView.findViewById(R.id.world_timeDay);
            день.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.сервер.выполнить_комманду(s.сервер.комманда_день);
                }
            });
            Button ночь = (Button) rootView.findViewById(R.id.world_timeNight);
            ночь.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.сервер.выполнить_комманду(s.сервер.комманда_ночь);
                }
            });
            final EditText время = rootView.findViewById(R.id.world_timeEditText);
            Button время_задать = rootView.findViewById(R.id.world_timeSet);
            Button время_добавить = rootView.findViewById(R.id.world_timeAdd);
            время_задать.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String комманда = s.сервер.комманда_задать_время.replace("%time%", время.getText().toString());
                    s.сервер.выполнить_комманду(комманда);
                }
            });
            время_добавить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String комманда = s.сервер.комманда_добавить_время.replace("%time%", время.getText().toString());
                    s.сервер.выполнить_комманду(комманда);
                }
            });
            Button погода_ясно = rootView.findViewById(R.id.world_weatherClear);
            погода_ясно.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String комманда = s.сервер.комманда_погода.replace("%weather%", "clear");
                    s.сервер.выполнить_комманду(комманда);
                }
            });
            Button погода_дождь = rootView.findViewById(R.id.world_weatherRain);
            погода_дождь.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String комманда = s.сервер.комманда_погода.replace("%weather%", "rain");
                    s.сервер.выполнить_комманду(комманда);
                }
            });
            Button белый_список_включить = rootView.findViewById(R.id.world_whitelist_on);
            белый_список_включить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s.сервер.выполнить_комманду("whitelist on");
                }
            });
            Button белый_список_выключить = rootView.findViewById(R.id.world_whitelist_off);
            белый_список_выключить.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    s.сервер.выполнить_комманду("whitelist off");
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ServerInfoFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    return ServerInfoFragment.newInstance(position + 1);
                case 1:
                    return ServerPlayersFragment.newInstance(position + 1);
                case 2:
                    return ServerResFragment.newInstance(position + 1);
                case 3:
                    return ServerWorldFragment.newInstance(position + 1);
            }
            return ServerInfoFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.serverActivity_info);
                case 1:
                    return getString(R.string.serverActivity_players);
                case 2:
                    return getString(R.string.serverActivity_resources);
                case 3:
                    return getString(R.string.serverActivity_world);
            }
            return null;
        }
    }

    private void обновить() {
        class Поток extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... params) {
                Boolean b = сервер.update();
                fe.saveFile(сервер.получить_токен() + ".json", сервер.toJSON());
                return b;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (!aBoolean) {
                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
                try{
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    setTitle(сервер.имя_сервера + " (" + сервер.id + ")");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        Поток поток = new Поток();
        поток.execute();
    }

    private void удалить() {
        deleteFile(сервер.получить_токен() + ".json");
        Toast.makeText(getApplicationContext(), R.string.serverRemoved, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("server_updated", обновлён);
        outState.putString("server", сервер.toJSON());
        super.onSaveInstanceState(outState);
    }
}
