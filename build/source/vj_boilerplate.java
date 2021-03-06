import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import codeanticode.syphon.*; 
import controlP5.*; 
import themidibus.*; 
import oscP5.*; 
import netP5.*; 
import processing.net.*; 
import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class vj_boilerplate extends PApplet {

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

PGraphics c;
int cw = 1280, ch = 720;

SyphonServer syphonserver;
SyphonClient[] syphon_clients;
int syphon_clients_index; //current syphon client
String syphon_name = "boilerplate", osc_address = syphon_name;
Log log;

public void settings() {
  size(500, 500, P3D);
}

public void setup() {
  log = new Log();

  midi_devices = midi.availableInputs();
  controlSetup();
  updateOSC(port);
  c = createGraphics(cw, ch, P3D);
  vp = new Viewport(c, 400, 50, 70);
  vp.resize(c);
  syphonserver = new SyphonServer(this, syphon_name);
}

public void draw() {
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

public PVector mapXYToCanvas(int x_in, int y_in, Viewport viewport, PGraphics pg) {
  int x_min = round(viewport.position.x + viewport.canvas_offset.x);
  int x_max = x_min + viewport.canvas_width;
  int y_min = round(viewport.position.y + viewport.canvas_offset.y);
  int y_max = y_min + viewport.canvas_height;
  PVector out = new PVector(-1, -1);
  if (x_in >= x_min && x_in <= x_max && y_in >= y_min && y_in <= y_max) {
    float x = map(x_in, x_min, x_max, 0.0f, pg.width);
    float y = map(y_in, y_min, y_max, 0.0f, pg.height);
    out = new PVector(x,y);
  }
  return out;
}

public void drawGraphics() {
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
class Log {
  String current_log;
  int counter;
  Log() {
    current_log = "No new events";
    counter = 30;
  }

  public void update() {
    fill(5);
    text(current_log, 10, height-10);
  }

  public void setText(String input) {
    String time = zeroFormat(hour()) + ":" + zeroFormat(minute()) + ":" + zeroFormat(second());
    current_log = time + " " + input;
  }
}
//function for formatting int values as strings: 1 becomes "01", 2 becomes "02"
public String zeroFormat(int input) {
  String output = Integer.toString(input);
  if (input < 10) output = "0" + output;
  return output;
}
class Viewport {
  int canvas_width;
  int canvas_height;
  int size; //viewport size
  PVector position;
  PVector canvas_offset = new PVector(0,0); //canvas pos within viewport
  PGraphics bg; //background customized for canvas

  Viewport(PGraphics pg, int vsize, int vpx, int vpy) {
    size = vsize;
    position = new PVector(vpx, vpy);
  }

  public void display(PGraphics pg) {
    pushMatrix();
    translate(position.x, position.y);
    noFill();
    stroke(100);
    rect(0, 0, size, size);
    noStroke();
    fill(255);
    drawPointers();

    if (viewport_show_alpha) image(bg, canvas_offset.x, canvas_offset.y, canvas_width, canvas_height);
    else {
      fill(0);
      rect(canvas_offset.x, canvas_offset.y, canvas_width, canvas_height);
    }
    image(pg, canvas_offset.x, canvas_offset.y, canvas_width, canvas_height);
    popMatrix();
  }

  public void resize(PGraphics pg) {
    int[] dims = scaleToFit(pg.width, pg.height, size, size);
    canvas_offset = new PVector(dims[0], dims[1]);
    canvas_width = dims[2];
    canvas_height =dims[3];
    bg = createAlphaBackground(canvas_width, canvas_height);
  }

  public PGraphics createAlphaBackground(int w, int h) {

    PGraphics abg = createGraphics(w, h, P2D);
    int s = 10; // size of square
    abg.beginDraw();
    abg.background(127+50);
    abg.noStroke();
    abg.fill(127-50);
    for (int x = 0; x < w; x+=s+s) {
      for (int y = 0; y < h; y+=s+s) {
        abg.rect(x, y, s, s);
      }
    }
    for (int x = s; x < w; x+=s+s) {
      for (int y = s; y < h; y+=s+s) {
        abg.rect(x, y, s, s);
      }
    }
    abg.endDraw();
    return abg;
  }

  public void drawPointers() {
    float x = canvas_offset.x;
    float y = canvas_offset.y;
    triangle(x, y, x-5, y, x, y-5);
    x += bg.width;
    triangle(x, y, x+5, y, x, y-5);
    y += bg.height;
    triangle(x, y, x+5, y, x, y+5);
    x = canvas_offset.x;
    triangle(x, y, x-5, y, x, y+5);
  }
}

public void updateCanvas() {
  c = createGraphics(cw, ch, P3D);
  vp.resize(c);
}

public void updateCanvas(int w, int h) {
  c = createGraphics(w, h, P3D);
  c = createGraphics(w, h, P3D);
  vp.resize(c);
}

public int[] scaleToFill(int in_w, int in_h, int dest_w, int dest_h) {
  PVector in = new PVector((float)in_w, (float)in_h); //vector of input dimensions
  PVector dest = new PVector((float)dest_w, (float)dest_h); //vector of destination dimensions
  /*
  calculate the scaling ratios for both axis, and choose the largest for scaling
  the output dimensions to FILL the destination
  */
  float scale = max(dest.x/in.x, dest.y/in.y);
  int out_w = round(in_w *scale);
  int out_h = round(in_h *scale);
  int off_x = (dest_w - out_w) / 2;
  int off_y = (dest_h - out_h) / 2;

  int[] out = {off_x, off_y, out_w, out_h};
  return out;
}

public int[] scaleToFit(int in_w, int in_h, int dest_w, int dest_h) {
  PVector in = new PVector((float)in_w, (float)in_h); //vector of input dimensions
  PVector dest = new PVector((float)dest_w, (float)dest_h); //vector of destination dimensions
  /*
  calculate the scaling ratios for both axis, and choose the SMALLEST for scaling
  the output dimensions to FIT the destination
  */
  float scale = min(dest.x/in.x, dest.y/in.y);
  int out_w = round(in_w *scale);
  int out_h = round(in_h *scale);
  int off_x = (dest_w - out_w) / 2;
  int off_y = (dest_h - out_h) / 2;
  println("offset x:", off_x, "offset y:", off_y);

  int[] out = {off_x, off_y, out_w, out_h};
  return out;
}
public void controlSetup() {
  cp5 = new ControlP5(this);
  int xoff = 10;
  int yoff = 20;

  cb = new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      switch(theEvent.getAction()) {
        case(ControlP5.ACTION_ENTER):
        cursor(HAND);
        break;
        case(ControlP5.ACTION_LEAVE):
        case(ControlP5.ACTION_RELEASEDOUTSIDE):
        cursor(ARROW);
        break;
      }
    }
  };

  cp5.getTab("default")
  .setAlwaysActive(true)
  .hideBar()
  .setWidth(-3)
  ;
  //hide default bar
  cp5.addTab("output/syphon").setActive(true);

  cp5.addTab("osc/midi")
  ;

  field_cw = cp5.addTextfield("field_cw")
  .setPosition(xoff, yoff)
  .setSize(30, 20)
  .setAutoClear(false)
  .setText(Integer.toString(cw))
  .setLabel("width")
  .setId(-1)
  .moveTo("output/syphon")
  ;

  xoff += field_cw.getWidth() + 10;
  field_ch = cp5.addTextfield("field_ch")
  .setPosition(xoff, yoff)
  .setSize(30, 20)
  .setAutoClear(false)
  .setText(Integer.toString(ch))
  .setLabel("height")
  .setId(-1)
  .moveTo("output/syphon")
  ;

  xoff += field_ch.getWidth() + 10;
  dropdown_syphon_client = cp5.addScrollableList("dropdown_syphon_client")
  .setPosition(xoff, yoff)
  .setSize(60, 100)
  .setBarHeight(20)
  .setItemHeight(20)
  .moveTo("output/syphon")
  .setOpen(false)
  .setLabel("syphon input")
  .setType(ScrollableList.LIST) // currently supported DROPDOWN and LIST
  ;
  dropdown_syphon_client.addCallback(new CallbackListener() {
    public void controlEvent(CallbackEvent theEvent) {
      /*
      if (theEvent.getAction()==ControlP5.ACTION_RELEASE && !dropdown_syphon_client.isOpen()) {
        updateSyphonClients();
      }
      else
*/
      if (theEvent.getAction() == ControlP5.ACTION_RELEASEDOUTSIDE) {
        dropdown_syphon_client.close();
      }
    }
  }
  );

  xoff += cp5.getController("dropdown_syphon_client").getWidth() + 10;
  field_syphon_name = cp5.addTextfield("field_syphon_name")
  .setPosition(xoff, yoff)
  .setSize(60, 20)
  .setAutoClear(false)
  .setText(syphon_name)
  .setLabel("syphon name")
  .setId(-1)
  .moveTo("output/syphon")
  ;

  xoff += field_syphon_name.getWidth() + 10;
  toggle_view_bg = cp5.addToggle("viewport_show_alpha")
  .setPosition(xoff, yoff)
  .setSize(50, 20)
  .setValue(viewport_show_alpha)
  .setLabel("alpha / none")
  .setMode(ControlP5.SWITCH)
  .setId(-1)
  .moveTo("output/syphon")
  ;

  xoff = 10; //reset position for tab "osc/midi"
  button_ip = cp5.addButton("button_ip")
  .setPosition(xoff, yoff)
  .setSize(70, 20)
  .setLabel("ip: " + ip)
  .setSwitch(false)
  .setId(-1)
  .moveTo("osc/midi")
  ;

  xoff += button_ip.getWidth() + 10;
  field_osc_port = cp5.addTextfield("field_osc_port")
  .setPosition(xoff, yoff)
  .setSize(30, 20)
  .setAutoClear(false)
  .setText(Integer.toString(port))
  .setLabel("osc port")
  .setId(-1)
  .moveTo("osc/midi")
  ;

  xoff += field_osc_port.getWidth() + 10;
  field_osc_address = cp5.addTextfield("field_osc_address")
  .setPosition(xoff, yoff)
  .setSize(50, 20)
  .setAutoClear(false)
  .setText(syphon_name)
  .setLabel("osc address")
  .setId(-1)
  .moveTo("osc/midi")
  ;

  xoff += field_osc_address.getWidth() + 10;
  toggle_log_osc = cp5.addToggle("log_osc")
  .setPosition(xoff, yoff)
  .setSize(30, 20)
  .setLabel("log osc")
  .setValue(true)
  .setId(-1)
  .moveTo("osc/midi")
  ;

  xoff += toggle_log_osc.getWidth() + 10;
  dropdown_midi = cp5.addScrollableList("dropdown_midi")
  .setPosition(xoff, yoff)
  .setSize(200, 100)
  .setOpen(false)
  .setBarHeight(20)
  .setItemHeight(20)
  .addItems(Arrays.asList(midi_devices))
  .setLabel("MIDI INPUT")
  .setId(-1)
  .moveTo("osc/midi")
  .setType(ScrollableList.LIST) // currently supported DROPDOWN and LIST
  ;

  xoff += dropdown_midi.getWidth() + 10;
  toggle_log_midi = cp5.addToggle("log_midi")
  .setPosition(xoff, yoff)
  .setSize(30, 20)
  .setLabel("log midi")
  .setValue(true)
  .setId(-1)
  .moveTo("osc/midi")
  ;

  /*  CUSTOM CONTROLS
  Add your own controls below. Use .setId(-1) to make controller
  unreachable by OSC.
  */
  xoff = 10;
  yoff = 300;
}

// checks if input is 4 digits
public int evalFieldInput1(String in, int current, Controller con) {
  String name = con.getLabel();
  int out = -1;
  char[] ints = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  char[] input = in.toCharArray();

  String txt = "value not int between 1 and 9999";
  if (input.length < 5) {
    int check = 0;
    for (char ch : input) {
      for (char i : ints) {
        if (ch == i) check++;
      }
    }

    if (input.length == check) {
      int verified_int = Integer.parseInt(in);
      txt = name + " changed from " + current + " to " + verified_int;
      if (verified_int < 1) {
        verified_int = 1;
        txt = name + " was lower than 0 and defaults to " + verified_int;
      }
      if (verified_int == current) txt = "value is not different from " + current;
      else {
        out = verified_int;
      }
    }
  }
  log.setText(txt);

  return out;
}

// checks if input is valid string for osc path
public boolean evalFieldInput2(String in, String current, Controller con) {
  String name = con.getLabel();
  String txt = "input to " + name + " is unchanged";
  boolean out = true;
  char[] illegal_chars = {'/', ',', '.', '(', ')', '[', ']',
  '{', '}', ' '};
  char[] input = in.toCharArray();
  if (!in.equals(current)) {
    if (input.length > 0) {
      for (char ch : input) {
        for (char i : illegal_chars) {
          if (ch == i) {
            txt = "input to " + name + " contained illegal character and was reset";
            out = false;
          }
        }
      }
    }
  }

  log.setText(txt);

  return out;
}

public void field_cw(String theText) {
  int value = evalFieldInput1(theText, cw, cp5.getController("field_cw"));
  if (value > 0) {
    cw = value;
    updateCanvas();
  }
}
public void field_ch(String theText) {
  int value = evalFieldInput1(theText, ch, cp5.getController("field_ch"));
  if (value > 0) {
    ch = value;
    updateCanvas();
  }
}

public void field_syphon_name(String input) {
  if (evalFieldInput2(input, syphon_name, field_syphon_name)) {
    syphon_name = input;
    field_osc_address.setText(input);
    osc_address = input;
    log.setText("syphon name and osc address set to " + input);
  }
  else field_syphon_name.setText(syphon_name);
}

public void field_osc_address(String input) {
  if (evalFieldInput2(input, osc_address, field_osc_address)) {
    syphon_name = input;
    log.setText("osc address set to " + input);
  }
  else field_osc_address.setText(osc_address);
}

public void dropdown_midi(int n) {
  updateMIDI(n);
  println("added " + midi_devices[n], n);
}

public void log_midi(boolean state) {
  log_midi = state;
  if (state) log.setText("started logging midi input");
  else log.setText("stopped logging midi input");
}

public void field_osc_port(String theText) {
  int value = evalFieldInput1(theText, port, field_osc_port);
  if (value > 0) {
    port = value;
    updateOSC(port);
  }
}

public void button_ip() {
  updateIP();
  log.setText("ip adress has been updated to " + ip);
}
/* syphon input wip
void dropdown_syphon_client(int n) {

  println("//////// dropdown syphon clients /////");
  println("dropdown index:", n);
  println("dropdown syphon client name:", (String)dropdown_syphon_client.getItem(n).get("name"));
  println("dropdown syphon client value:", (int)dropdown_syphon_client.getItem(n).get("value"));

  syphon_clients_index = (int)dropdown_syphon_client.getItem(n).get("value");
  dropdown_syphon_client.setLabel((String)dropdown_syphon_client.getItem(n).get("name"));
  dropdown_syphon_client.close();
}

void updateSyphonClients() {
  dropdown_syphon_client.clear();

  HashMap<String, String>[] hm_array = SyphonClient.listServers();

  syphon_clients = new SyphonClient[SyphonClient.listServers().length];
  String a_name, s_name;
  println(SyphonClient.listServers());
  println("adding syphon inputs to dropdown");
  for (int i = 0; i < SyphonClient.listServers().length; i++) {
    s_name = hm_array[i].get("ServerName");
    a_name = hm_array[i].get("AppName");
    dropdown_syphon_client.addItem(a_name + " " + s_name, i);
    println("syphon client added:", a_name + " " + s_name, "#", i);
  }
  dropdown_syphon_client.addItem("no input", -1);
}
*/
public void noteOn(int channel, int pitch, int velocity) {
  if (log_midi) log.setText("Note On // Channel:"+channel + " // Pitch:"+pitch + " // Velocity:"+velocity);
}

public void noteOff(int channel, int pitch, int velocity) {
  if (log_midi) log.setText("Note Off // Channel:"+channel + " // Pitch:"+pitch + " // Velocity:"+velocity);
}

public void controllerChange(int channel, int number, int value) {
  if (log_midi) log.setText("Slider // Channel:"+channel + " // Number:" +number + " // Value: "+value);
}

public void changeSlider(String name, int value) {
  Controller con = cp5.getController(name);
  con.setValue(map(value, 0, 127, con.getMin(), con.getMax()));
}

public void updateMIDI(int n) {
 log.setText("added midi device " + midi_devices[n]);
 midi = new MidiBus(this, n, -1);
}
public void updateOSC(int p) {
  updateIP();
  oscP5 = new OscP5(this, p);
  cp5.getController("field_osc_port").setValue(p);
}

public void updateIP() {
  ip = Server.ip();
  cp5.getController("button_ip").setLabel("ip: " + ip);
}

public void oscEvent(OscMessage theOscMessage) {
  String str_in[] = split(theOscMessage.addrPattern(), '/');
  String txt = "got osc message: " + theOscMessage.addrPattern();
  if (str_in.length == 3) {
    if (str_in[1].equals(osc_address) &&
    cp5.getController(str_in[2]) != null &&
    cp5.getController(str_in[2]).getId() != -1)
    {
      Controller con = cp5.getController(str_in[2]);

      if (theOscMessage.checkTypetag("i")) {
        int value = theOscMessage.get(0).intValue();
        value = constrain(value, (int)con.getMin(), (int)con.getMax());
        con.setValue(value);
        txt += " int value: " + Integer.toString(value);
      }

      else if (theOscMessage.checkTypetag("f")) {
        float value = theOscMessage.get(0).floatValue();
        value = constrain(value, con.getMin(), con.getMax());
        con.setValue(value);
        txt += " float value: " + Float.toString(value);
      }
    }
  }
  if (log_osc) log.setText(txt);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "vj_boilerplate" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
