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
Textfield field_cw, field_ch, field_syphon_name, field_osc_port, field_osc_address;
Button button_ip;
Viewport view;
boolean viewport_show_alpha = false;
boolean log_midi = true, log_osc = true;

int port = 9999;
String ip;

PGraphics c;
int cw = 1280, ch = 720;

SyphonServer syphonserver;
String syphon_name = "boilerplate", osc_address = syphon_name;
Log log;

int n;
float t1, 
  t2, 
  speed1, 
  speed2, 
  x1_range1, 
  x1_range2, 
  y1_range1, 
  y1_range2, 
  x2_range1, 
  x2_range2, 
  y2_range1, 
  y2_range2
  ;

void settings() {
  size(500, 500, P3D);
}

void setup() {
  log = new Log();

  midi_devices = midi.availableInputs();
  controlSetup();
  updateOSC(port);

  c = createGraphics(cw, ch, P3D);
  view = new Viewport(c, 400);
  syphonserver = new SyphonServer(this, syphon_name);
  view.resize(c);
}

void draw() {
  background(127);
  drawGraphics();
  view.display(c, 50, 50);
  syphonserver.sendImage(c);
  log.update();
}

void drawGraphics() {  
  c.beginDraw();
  c.clear();
  c.tint(255, 250);
  c.tint(255, 255);
  c.stroke(255);
  c.strokeWeight(5);
  c.translate (c.width/2, c.height/2);

  for (int i = 0; i < n; i++) {
    c.line(x1(t1 + i), y1(t1 + i), x2(t2 + i), y2(t2 + i));
  }
  c.endDraw();
  t1+=speed1;
  t2+=speed2;
}


float x1(float t) {
  return sin(t / 10) * c.width/2 * x1_range1 + sin(t / 3) * c.width/2 * x1_range2;
}

float y1(float t) {
  return cos(t / 10) * c.height/2 * y1_range1 + cos(t /3) * c.height/2 *y1_range2;
}

float x2(float t) {
  return sin(t / 10) * c.width/2 * x2_range1 + sin(t / 3) * c.width/2 * x2_range2;
}

float y2(float t) {
  return cos(t / 10) * c.height/2 * y2_range1 + cos(t /3) * c.height/2 *y2_range2;
}
