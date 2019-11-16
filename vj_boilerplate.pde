/*
This is a boilerplate for creating VJ apps with Processing
It creates a PGraphics "canvas" named c that you can draw graphics onto,
and outputs that canvas via Syphon.
The canvas is rescalable and renamable.

It relies on:
The Syphon library by Andres Colubri
The Midibus library by Severin Smith
oscP5 and controlP5 by Andreas Schlegel
*/
import codeanticode.syphon.*;
import controlP5.*;
import themidibus.*;
import oscP5.*;
import netP5.*;
import processing.net.*;
import java.util.*;

MidiBus midi;
String[] midi_devices;
OscP5 oscP5;
ControlP5 cp5;
CallbackListener cb;
Textfield field_cw, field_ch, field_syphon_name, field_osc_port, field_osc_address;
Button button_ip;
ScrollableList dropdown_midi, dropdown_syphon_client;
Toggle toggle_log_osc, toggle_log_midi, toggle_view_bg;
Viewport vp;
boolean viewport_show_alpha = false;
boolean log_midi = true, log_osc = true;

int port = 9999;
String ip;

PGraphics c, c_input;
int cw = 1280, ch = 720;

SyphonServer syphonserver;
SyphonClient[] syphon_clients;
int syphon_clients_index; //current syphon client
String syphon_name = "boilerplate", osc_address = syphon_name;
Log log;

void settings() {
  size(500, 500, P3D);
}

void setup() {
  log = new Log();

  midi_devices = midi.availableInputs();
  controlSetup();
  updateOSC(port);

  vp = new Viewport(c, 400, 50, 50);
  syphonserver = new SyphonServer(this, syphon_name);
  vp.resize(c);
}

void draw() {
  background(127);
  noStroke();
  fill(100);
  rect(0, 0, width, 55);
  fill(cp5.getTab("output/syphon").getColor().getBackground());
  rect(0, 0, width, cp5.getTab("output/syphon").getHeight());


  drawGraphics();
  vp.display(c);
  syphonserver.sendImage(c);
  log.update();
}

/*
mapXYToCanvas remaps an x/y position inside a viewport to a PGraphics canvas.
E.g to map mouse position within a canvas without scaling up the whole app.
*/

PVector mapXYToCanvas(int x_in, int y_in, Viewport viewport, PGraphics pg) {
  int x_min = round(viewport.position.x + viewport.canvas_offset.x);
  int x_max = x_min + viewport.canvas_width;
  int y_min = round(viewport.position.y + viewport.canvas_offset.y);
  int y_max = y_min + viewport.canvas_height;
  PVector out = new PVector(-1, -1);
  if (x_in >= x_min && x_in <= x_max && y_in >= y_min && y_in <= y_max) {
    float x = map(x_in, x_min, x_max, 0.0, pg.width);
    float y = map(y_in, y_min, y_max, 0.0, pg.height);
    out = new PVector(x,y);
  }
  return out;
}

void drawGraphics() {
  c.beginDraw();

/* syphon input wip
  if (isSyphonAvailable()){
    println("using syphon input:", syphon_clients_index);
    println(" // name:", (String)dropdown_syphon_client.getItem(syphon_clients_index).get("name"));
    if (syphon_clients[0].newFrame()) println("recieving from " + syphon_clients_index);
    //c_input = syphon_clients[syphon_clients_index].getGraphics(c_input);
    //c.image(c_input, 0,0,c_input.width, c_input.height);
  }
  */

  c.endDraw();
}
/* syphon input wip
boolean isSyphonAvailable() {
  boolean out = false;
  //println(syphon_clients_index);
  if (syphon_clients_index > -1) {
    out = true;
  }
  return out;
}
*/
