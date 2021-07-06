/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.langs.LanguagePlugin;
import io.github.rosemoe.editor.core.langs.empty.EmptyLanguage;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.plugins.color.ColorChooser;
import io.github.rosemoe.editor.plugins.color.ColorPlugin;
import io.github.rosemoe.editor.plugins.color.ColorPluginDarcula;
import io.github.rosemoe.editor.plugins.color.ColorPluginDebug;
import io.github.rosemoe.editor.plugins.color.ColorPluginEclipse;
import io.github.rosemoe.editor.plugins.color.ColorPluginGithub;
import io.github.rosemoe.editor.plugins.color.ColorPluginHTML;
import io.github.rosemoe.editor.plugins.color.ColorPluginNone;
import io.github.rosemoe.editor.plugins.color.ColorPluginNotepadXX;
import io.github.rosemoe.editor.plugins.color.ColorPluginSolarized;
import io.github.rosemoe.editor.plugins.color.ColorPluginVS2019;
import io.github.rosemoe.editor.plugins.langs.LanguageChooser;
import io.github.rosemoe.editor.plugins.langs.desc.CDescription;
import io.github.rosemoe.editor.plugins.langs.desc.CppDescription;
import io.github.rosemoe.editor.plugins.langs.html.HTMLLanguage;
import io.github.rosemoe.editor.plugins.langs.java.JavaLanguage;
import io.github.rosemoe.editor.plugins.langs.python.PythonLanguage;
import io.github.rosemoe.editor.plugins.langs.universal.UniversalLanguage;
import io.github.rosemoe.editor.utils.CrashHandler;

public class MainActivity extends AppCompatActivity {

    private CodeEditor editor;
    private LinearLayout panel;
    private EditText search, replace;

    private MainActivityModel mam = new MainActivityModel();

    protected  void loadLangs() {
        editor.plugins.put(
            //new UniversalLanguage(editor,new CDescription()),
            //new UniversalLanguage(editor,new CppDescription()),
            //new HTMLLanguage(editor),
            //new PythonLanguage(editor),
            //new EmptyLanguage(editor),
            new JavaLanguage(editor)
        );
    }
    protected void loadThemes() {
        editor.plugins.put(
            new ColorPluginEclipse(editor),
            new ColorPluginDarcula(editor),
            new ColorPluginVS2019(editor),
            new ColorPluginNotepadXX(editor),
            new ColorPluginHTML(editor),
            new ColorPluginSolarized(editor),
            new ColorPluginGithub(editor),
            new ColorPluginNone(editor),
            new ColorPluginDebug(editor)
        );
    }
    protected void setEditorLanguage(LanguagePlugin el, String fname) {
        editor.setEditorLanguage(el);
        new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(fname)));
                String line;
                StringBuilder text = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    text.append(line).append('\n');
                }
                runOnUiThread(() -> editor.setText(text));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    Menu menu = null;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.debug();
        CrashHandler.INSTANCE.init(this);
        setContentView(R.layout.activity_main);

        editor = CodeEditor.newInstance(this, R.id.editor_root_view);
        ((WidgetExtensionController)editor.systemPlugins.get("linenumberpanel")).addItemToMenu();
        ((WidgetExtensionController)editor.systemPlugins.get("symbolinput")).addItemToMenu();
        if ( menu != null ) { editor.attachMenu(menu); }

        loadThemes();
        loadLangs();

        panel = findViewById(R.id.search_panel);
        search = findViewById(R.id.search_editor);
        replace = findViewById(R.id.replace_editor);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.getSearcher().search(editable.toString());
            }
        });
        Logger.debug();
        editor.setTypefaceText(Typeface.MONOSPACE);
        editor.setOverScrollEnabled(false);
        //setEditorLanguage(new MkshLanguage(), "samples/mksh/mksh.txt");
        setEditorLanguage((LanguagePlugin) editor.plugins.get("Java"), "samples/java/java.txt");
        //setEditorLanguage(new HTMLLanguage(),"samples/html/html.txt");
        if ( Logger.DEBUG ) {
            new ColorPluginDebug(editor).apply();
        } else {
            ColorPlugin.DEFAULT(editor).apply();
        }
        Logger.debug();
        editor.setNonPrintablePaintingFlags(CodeEditor.FLAG_DRAW_WHITESPACE_LEADING | CodeEditor.FLAG_DRAW_LINE_SEPARATOR);
        langChoose = new LanguageChooser(editor);
        colorChooser = new ColorChooser(editor);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if ( editor != null ) {
            editor.attachMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    private LanguageChooser langChoose;
    private ColorChooser colorChooser;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean b = Logger.DEBUG;
        switch (item.getItemId()) {
            case R.id.text_undo:
                editor.undo();
                break;
            case R.id.text_redo:
                editor.redo();
                break;
            case R.id.goto_end:
                editor.setSelection(editor.getText().getLineCount() - 1, editor.getText().getColumnCount(editor.getText().getLineCount() - 1));
                break;
            case R.id.move_up:
                editor.moveSelectionUp();
                break;
            case R.id.move_down:
                editor.moveSelectionDown();
                break;
            case R.id.home:
                editor.moveSelectionHome();
                break;
            case R.id.end:
                editor.moveSelectionEnd();
                break;
            case R.id.move_left:
                editor.moveSelectionLeft();
                break;
            case R.id.move_right:
                editor.moveSelectionRight();
                break;
            case R.id.code_format:
                editor.formatCodeAsync();
                break;
            case R.id.switch_language:
                langChoose.showChooser();
                break;
            case R.id.search_panel_st:
                if (panel.getVisibility() == View.GONE) {
                    replace.setText("");
                    search.setText("");
                    editor.getSearcher().stopSearch();
                    panel.setVisibility(View.VISIBLE);
                    item.setChecked(true);
                } else {
                    panel.setVisibility(View.GONE);
                    editor.getSearcher().stopSearch();
                    item.setChecked(false);
                }
                break;
            case R.id.search_am:
                replace.setText("");
                search.setText("");
                editor.getSearcher().stopSearch();
                editor.beginSearchMode();
                break;
            case R.id.switch_colors:
                colorChooser.showChooser();
                break;
            case R.id.text_wordwrap:
                item.setChecked(!item.isChecked());
                editor.setWordwrap(item.isChecked());
                break;
            case R.id.open_logs: {
                FileInputStream fis = null;
                try {
                    fis = openFileInput("crash-journal.log");
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    Toast.makeText(this, "Succeeded", Toast.LENGTH_SHORT).show();
                    editor.setText(sb);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed:" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case R.id.clear_logs: {
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput("crash-journal.log", MODE_PRIVATE);
                    Toast.makeText(this, "Succeeded", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed:" + e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case R.id.open_debug_logs: {
                //editor.setText(Logs.getLogs());
                break;
            }
            case R.id.enable_logcat_logs:
                Logger.DEBUG = ! Logger.DEBUG;
                MenuItem mi = editor.view.findViewById(R.id.enable_logcat_logs);
                item.setChecked(Logger.DEBUG);
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    public void gotoNext(View view) {
        try {
            editor.getSearcher().gotoNext();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void gotoLast(View view) {
        try {
            editor.getSearcher().gotoLast();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replace(View view) {
        try {
            editor.getSearcher().replaceThis(replace.getText().toString());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceAll(View view) {
        try {
            editor.getSearcher().replaceAll(replace.getText().toString());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
