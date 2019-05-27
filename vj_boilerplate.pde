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
ScrollableList dropdown_midi, client_list;
Toggle toggle_log_osc, toggle_log_midi, toggle_view_bg;
Viewport view;
boolean viewport_show_alpha = false;
boolean log_midi = true, log_osc = true;

int port = 9999;
String ip;

PGraphics c, c_input;
int cw = 1280, ch = 720;

SyphonServer syphonserver;
SyphonClient[] syphon_clients;
int syphon_clients_index = -1;
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

  c = createGraphics(cw, ch, P3D);
  c_input = createGraphics(0,0,P3D);
  view = new Viewport(c, 400);
  syphonserver = new SyphonServer(this, syphon_name);
  view.resize(c);
}

void draw() {
  background(127);
  noStroke();
  fill(100);
  rect(0, 0, width, 55);
  fill(cp5.getTab("output/syphon").getColor().getBackground());
  rect(0, 0, width, cp5.getTab("output/syphon").getHeight());


  drawGraphics();
  view.display(c, 60, 50);
  syphonserver.sendImage(c);
  log.update();
}

void drawGraphics() {
  c.beginDraw();
  if (receiveSyphonInput()){
    c_input = syphon_clients[syphon_clients_index].getGraphics(c_input);
    c.image(c_input, 0,0,c_input.width, c_input.height);
  }

  c.endDraw();
}

boolean receiveSyphonInput() {
  boolean out = false;
  //println(syphon_clients_index);
  if (syphon_clients_index > -1 && syphon_clients[syphon_clients_index].newFrame()) {
    out = true;
  }
  return out;
}
