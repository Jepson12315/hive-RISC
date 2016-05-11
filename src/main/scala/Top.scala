import Chisel._

class Top extends Module {
  val io = new Bundle {
    val peripheral = new PeripheralIO
  }

  val tile = Module(new CorePipelined)

  val peripheral = Module(new Peripheral)
  io.peripheral <> peripheral.io.pin_hub

  // numbers
  peripheral.io.raw_hub.segment_display_raw := tile.io.display(15,0)
  // buttons
  tile.io.backward := peripheral.io.raw_hub.backward_raw.pulse
  tile.io.execute := peripheral.io.raw_hub.execute_raw.pulse
  tile.io.forward := peripheral.io.raw_hub.forward_raw.pulse
  // switchs
  tile.io.type_1 := peripheral.io.raw_hub.type_select_raw_first.pulse
  tile.io.type_2 := peripheral.io.raw_hub.type_select_raw_second.pulse
  tile.io.valueSelect := peripheral.io.raw_hub.value_select_raw.pulse
  tile.io.regID_0 := peripheral.io.raw_hub.regID_select_raw_0.pulse
  tile.io.regID_1 := peripheral.io.raw_hub.regID_select_raw_1.pulse
  tile.io.regID_2 := peripheral.io.raw_hub.regID_select_raw_2.pulse
  tile.io.regID_3 := peripheral.io.raw_hub.regID_select_raw_3.pulse
  tile.io.regID_4 := peripheral.io.raw_hub.regID_select_raw_4.pulse
  //leds
  peripheral.io.raw_hub.stage_if_raw := tile.io.stage_if
  peripheral.io.raw_hub.stage_dec_raw := tile.io.stage_dec
  peripheral.io.raw_hub.stage_exe_raw := tile.io.stage_exe
  peripheral.io.raw_hub.stage_mem_raw := tile.io.stage_mem
  peripheral.io.raw_hub.stage_wb_raw := tile.io.stage_wb
}

class TopTests(c: Top) extends Tester(c) {

  reset(1)
  for (i <- 0 until 5)
    peekAt(c.tile.d_cache.mem, i)
  step (10)
  //TODO: val prv_pc = peek(c.core.pc)

  peekAt(c.tile.d_cache.mem, 4)

  peek(c.peripheral.io.pin_hub.segment_display_pin.segment)
  /*
  poke(c.peripheral.io.pin_hub.execute_pin.button, true)
  poke(c.peripheral.io.pin_hub.type_select_pin_second.switch, true)
  step(5)
  poke(c.peripheral.io.pin_hub.value_select_pin.switch, true)
  step(5)
  poke(c.peripheral.io.pin_hub.value_select_pin.switch, false)
  step(5)
  poke(c.peripheral.io.pin_hub.execute_pin.button, true)
  poke(c.peripheral.io.pin_hub.type_select_pin_second, false)
  step(5)
  poke(c.peripheral.io.pin_hub.value_select_pin.switch, true)
  step(5)
  poke(c.peripheral.io.pin_hub.value_select_pin.switch, false)
  step(5)
  poke(c.peripheral.io.pin_hub.execute_pin.button, true)
  */

  //see

  def see: Unit = {
    //peek()
    //peekAt()
  }
}

/*
object Top {
  def main(args: Array[String]): Unit = {
    val tutArgs = args.slice(1, args.length)
    args.foreach(arg => println(arg))
    chiselMainTest(tutArgs, () => Module(new Top())) {
      c => new TopTests(c) }
  }
}*/
object Top {
  def main(args: Array[String]): Unit = {
    args.foreach(arg => println(arg))
    chiselMainTest(args, () => Module(new Top())) {
      c => new TopTests(c) }
  }
}
