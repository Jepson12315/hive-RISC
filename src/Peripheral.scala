import Chisel._

/* steps to add a peripheral device
 1. declare DeviceName DeviceNameRaw DeviceNamePin class
 2. add the above Raw/Pin to the PeripheralLink/IO
 3. create instances under Peripheral class
 4. interconnect instances.pin/link to Peripheral.io.pin_hub/link_hub
 5. PeripheralIO is automatically connected to the TopIO
*/

class PeripheralLink extends Bundle {
  // TODO: a temporarily bypassed version of data link, following lines should be inside the class, see below
  val segment_display_raw = UInt(INPUT, 16)
  // Select display type
  val type_select_raw_first = new SwitchPulseRaw()
  val type_select_raw_second = new SwitchPulseRaw()
  // High bits or low bits
  val value_select_raw = new SwitchPulseRaw()
  // Select regID
  val regID_select_raw_0 = new SwitchPulseRaw()
  val regID_select_raw_1 = new SwitchPulseRaw()
  val regID_select_raw_2 = new SwitchPulseRaw()
  val regID_select_raw_3 = new SwitchPulseRaw()
  val regID_select_raw_4 = new SwitchPulseRaw()
  // Buttons
  val forward_raw = new ButtonPulseRaw()
  val execute_raw = new ButtonPulseRaw()
  val backward_raw = new ButtonPulseRaw()
  // LEDs
  val stage_if_raw = Bool(INPUT)
  val stage_dec_raw = Bool(INPUT)
  val stage_exe_raw = Bool(INPUT)
  val stage_mem_raw = Bool(INPUT)
  val stage_wb_raw = Bool(INPUT)
}

class PeripheralIO extends Bundle {
  val segment_display_pin = new SegmentDisplayPin()
  // Select display type
  val type_select_pin_first = new SwitchPulsePin()
  val type_select_pin_second = new SwitchPulsePin()
  // High bits or low bits
  val value_select_pin = new SwitchPulsePin()
  // Select regID to display
  val regID_select_pin_0 = new SwitchPulsePin()
  val regID_select_pin_1 = new SwitchPulsePin()
  val regID_select_pin_2 = new SwitchPulsePin()
  val regID_select_pin_3 = new SwitchPulsePin()
  val regID_select_pin_4 = new SwitchPulsePin()
  // LEDs
  val stage_if_pin = new LEDPulsePin()
  val stage_dec_pin = new LEDPulsePin()
  val stage_exe_pin = new LEDPulsePin()
  val stage_mem_pin = new LEDPulsePin()
  val stage_wb_pin = new LEDPulsePin()
  val forward_led = Bool(OUTPUT)
  val backward_led = Bool(OUTPUT)
  val execute_led = Bool(OUTPUT)
  // Buttons
  val forward_pin = new ButtonPulsePin()
  val execute_pin = new ButtonPulsePin()
  val backward_pin = new ButtonPulsePin()
}

class Peripheral extends Module{
  val io = new Bundle() {
    val raw_hub = new PeripheralLink
    val pin_hub = new PeripheralIO
  }
  // TODO: interpreting data from PeripheralLink and assign following values
  // val segment_display_raw = UInt(0, 16)
  // val analog_monitor_raw = new AnalogMonitorRaw()

  val segment_display = Module(new SegmentDisplay)
  segment_display.io.pin <> io.pin_hub.segment_display_pin
  segment_display.io.raw <> io.raw_hub.segment_display_raw

  val v_clk_25m = new Clock(this.reset)

  // Select display type
  val switch_type_1 = Module(new SwitchPulse)
  switch_type_1.io.raw<>io.raw_hub.type_select_raw_first
  switch_type_1.io.pin<>io.pin_hub.type_select_pin_first

  val switch_type_2 = Module(new SwitchPulse)
  switch_type_2.io.raw<>io.raw_hub.type_select_raw_second
  switch_type_2.io.pin<>io.pin_hub.type_select_pin_second

  // High bits or low bits
  val switch_value = Module(new SwitchPulse)
  switch_value.io.raw<>io.raw_hub.value_select_raw
  switch_value.io.pin<>io.pin_hub.value_select_pin

  // Select regID
  val regID_0 = Module(new SwitchPulse)
  regID_0.io.raw<>io.raw_hub.regID_select_raw_0
  regID_0.io.pin<>io.pin_hub.regID_select_pin_0

  val regID_1 = Module(new SwitchPulse)
  regID_1.io.raw<>io.raw_hub.regID_select_raw_1
  regID_1.io.pin<>io.pin_hub.regID_select_pin_1

  val regID_2 = Module(new SwitchPulse)
  regID_2.io.raw<>io.raw_hub.regID_select_raw_2
  regID_2.io.pin<>io.pin_hub.regID_select_pin_2

  val regID_3 = Module(new SwitchPulse)
  regID_3.io.raw<>io.raw_hub.regID_select_raw_3
  regID_3.io.pin<>io.pin_hub.regID_select_pin_3

  val regID_4 = Module(new SwitchPulse)
  regID_4.io.raw<>io.raw_hub.regID_select_raw_4
  regID_4.io.pin<>io.pin_hub.regID_select_pin_4

  // Buttons
  val backward_pulse = Module(new ButtonAntijitterPulse)
  backward_pulse.io.raw<>io.raw_hub.backward_raw
  backward_pulse.io.pin<>io.pin_hub.backward_pin

  val execute_pulse = Module(new ButtonAntijitterPulse)
  execute_pulse.io.raw<>io.raw_hub.execute_raw
  execute_pulse.io.pin<>io.pin_hub.execute_pin

  val forward_pulse = Module(new ButtonAntijitterPulse)
  forward_pulse.io.raw<>io.raw_hub.forward_raw
  forward_pulse.io.pin<>io.pin_hub.forward_pin

  io.pin_hub.backward_led := io.pin_hub.backward_pin.button
  io.pin_hub.execute_led := io.pin_hub.execute_pin.button
  io.pin_hub.forward_led := io.pin_hub.forward_pin.button

  val stage_if_pulse = Module(new LEDPulse)
  stage_if_pulse.io.raw<>io.raw_hub.stage_if_raw
  stage_if_pulse.io.pin<>io.pin_hub.stage_if_pin

  val stage_dec_pulse = Module(new LEDPulse)
  stage_dec_pulse.io.raw<>io.raw_hub.stage_dec_raw
  stage_dec_pulse.io.pin<>io.pin_hub.stage_dec_pin

  val stage_exe_pulse = Module(new LEDPulse)
  stage_exe_pulse.io.raw<>io.raw_hub.stage_exe_raw
  stage_exe_pulse.io.pin<>io.pin_hub.stage_exe_pin

  val stage_mem_pulse = Module(new LEDPulse)
  stage_mem_pulse.io.raw<>io.raw_hub.stage_mem_raw
  stage_mem_pulse.io.pin<>io.pin_hub.stage_mem_pin

  val stage_wb_pulse = Module(new LEDPulse)
  stage_wb_pulse.io.raw<>io.raw_hub.stage_wb_raw
  stage_wb_pulse.io.pin<>io.pin_hub.stage_wb_pin

}

class SegmentDisplayPin extends Bundle {
  val segment = UInt(OUTPUT, width = 7)
  val select = UInt(OUTPUT, width = 4)
  val dot = UInt(OUTPUT, width = 1)
}

class SegmentDisplay extends Module {
  val io = new Bundle {
    val raw = UInt(INPUT, 16)
    val pin = new SegmentDisplayPin
  }

  io.pin.select := UInt("b1111")
  io.pin.segment := UInt("b111_1111")
  io.pin.dot := UInt("b1")

  val counter = Reg(init = UInt(0, 19))
  counter := counter + UInt(1)

  val digit = UInt()
  digit := UInt("h_F")
  switch (counter(18,17)) {
    is(UInt(0)) { digit := io.raw( 3, 0); io.pin.select := UInt("b1110") }
    is(UInt(1)) { digit := io.raw( 7, 4); io.pin.select := UInt("b1101") }
    is(UInt(2)) { digit := io.raw(11, 8); io.pin.select := UInt("b1011") }
    is(UInt(3)) { digit := io.raw(15,12); io.pin.select := UInt("b0111") }
  }
  switch (digit) {
    is(UInt("h_0")) { io.pin.segment := UInt("b1000000") }
    is(UInt("h_1")) { io.pin.segment := UInt("b1111001") }
    is(UInt("h_2")) { io.pin.segment := UInt("b0100100") }
    is(UInt("h_3")) { io.pin.segment := UInt("b0110000") }
    is(UInt("h_4")) { io.pin.segment := UInt("b0011001") }
    is(UInt("h_5")) { io.pin.segment := UInt("b0010010") }
    is(UInt("h_6")) { io.pin.segment := UInt("b0000010") }
    is(UInt("h_7")) { io.pin.segment := UInt("b1111000") }
    is(UInt("h_8")) { io.pin.segment := UInt("b0000000") }
    is(UInt("h_9")) { io.pin.segment := UInt("b0010000") }
    is(UInt("h_A")) { io.pin.segment := UInt("b0001000") }
    is(UInt("h_B")) { io.pin.segment := UInt("b0000011") }
    is(UInt("h_C")) { io.pin.segment := UInt("b1000110") }
    is(UInt("h_D")) { io.pin.segment := UInt("b0100001") }
    is(UInt("h_E")) { io.pin.segment := UInt("b0000110") }
    is(UInt("h_F")) { io.pin.segment := UInt("b0001110") }
  }
}

class SwitchPulseRaw extends Bundle {
  val pulse = Bool(OUTPUT)
}

class SwitchPulsePin extends Bundle {
  val switch = Bool(INPUT)
}

class SwitchPulse extends Module {
  val io = new Bundle {
    val raw = new SwitchPulseRaw
    val pin = new SwitchPulsePin
  }
  io.raw.pulse := io.pin.switch
}

class LEDPulsePin extends Bundle {
  val pulse = Bool(OUTPUT)
}

class LEDPulse extends Module {
  val io = new Bundle {
    val raw = Bool(INPUT)
    val pin = new LEDPulsePin
  }
  io.pin.pulse := io.raw
}

class ButtonPulseRaw extends Bundle {
  val pulse = Bool(OUTPUT)
}

class ButtonPulsePin extends Bundle {
  val button = Bool(INPUT)
}

class ButtonAntijitterPulse extends Module{
  val io = new Bundle {
    val raw = new ButtonPulseRaw
    val pin = new ButtonPulsePin
  }

  val counter = RegInit(UInt(0,width=32))
  val currentOutputStatus = RegInit(Bool(false))

  val lastStatus = RegNext(io.pin.button)
  when(io.pin.button === lastStatus){
    counter := counter + UInt(1)
    when(counter===UInt(10000)){
      //  when(counter===UInt(100000)){
      when(currentOutputStatus === Bool(false) & io.pin.button === Bool(true)){
        io.raw.pulse := Bool(true)
      }.otherwise{
        io.raw.pulse := Bool(false)
      }
      currentOutputStatus := io.pin.button
    }.otherwise{
      io.raw.pulse := Bool(false)
    }
  }.otherwise{
    counter := UInt(0)
    io.raw.pulse := Bool(false)
  }
  printf("pulse : %d current output:%d lastStatus:%d input:%d counter:%d\n",io.raw.pulse,currentOutputStatus,lastStatus,io.pin.button,counter)
}
