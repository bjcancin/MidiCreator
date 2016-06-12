package com.example.android.midicreator;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends FragmentActivity {

    Button next_button;
    TextView help_title, help_paragraph_1, help_paragraph_2;
    int help_count;
    String create, create_1, create_2, edit, edit_1, edit_2, options, options_1, options_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ////////////////
        // Declaraciones
        ////////////////

        help_count = 1;

        create = "Crear";
        create_1 = "Para crear un ritmo, presiona el botón \"Crear/Editar\" en la pantalla de inicio. Debes ingresar un nombre mayor o igual a un caracter y el ritmo sólo debe contener \"o\" (tiempo fuerte), \"x\" (tiempo débil) o \".\" silencio. Por ejemplo un ritmo aceptado sería \"xx.ox.\". La aplicación dará aviso en caso de que el ritmo no sea correctamente almacenado.";
        create_2 = "";

        edit = "Editar";
        edit_1 = "Para editar un ritmo, presiona el botón \"Crear/Editar\" en la pantalla de inicio. Debes seleccionar de la lista el ritmo a editar. Puedes editar el ritmo, siguiendo las mismas reglas que al crear un ritmo, o también puedes eliminar el ritmo.";
        edit_2 = "";

        options = "Opciones";
        options_1 = "Para acceder a las opciones dirígete al menú que se encuentra en la esquina superior derecha y selecciona opciones.";
        options_2 = "";

        //////////////////
        // Botones y Texto
        /////////////////

        help_title = (TextView) findViewById(R.id.help_title);
        help_paragraph_1 = (TextView) findViewById(R.id.help_paragraph_1);
        help_paragraph_2 = (TextView) findViewById(R.id.help_paragraph_2);

        next_button = (Button) findViewById(R.id.next_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (help_count){
                    case 1:
                        help_title.setText(create);
                        help_paragraph_1.setText(create_1);
                        help_paragraph_2.setText(create_2);
                        help_count++;
                        break;

                    case 2:
                        help_title.setText(edit);
                        help_paragraph_1.setText(edit_1);
                        help_paragraph_2.setText(edit_2);
                        help_count++;
                        break;

                    case 3:
                        help_title.setText(options);
                        help_paragraph_1.setText(options_1);
                        help_paragraph_2.setText(options_2);
                        next_button.setText("Salir");
                        help_count++;
                        break;

                    default:
                        finish();
                        break;
                }
            }
        });
    }

}
