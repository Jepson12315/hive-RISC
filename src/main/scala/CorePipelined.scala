import Chisel._

// TODO: MEMIFCE: put dmem imem outside the core, or not?
class CorePipelined extends Module{
  val io = new Bundle {
    // buttons
    val backward = Bool(INPUT)
    val execute = Bool(INPUT)
    val forward = Bool(INPUT)
    // switchs
    val type_1 = Bool(INPUT)
    val type_2 = Bool(INPUT)
    val valueSelect = Bool(INPUT)
    val regID_0 = Bool(INPUT)
    val regID_1 = Bool(INPUT)
    val regID_2 = Bool(INPUT)
    val regID_3 = Bool(INPUT)
    val regID_4 = Bool(INPUT)
    // numbers
    val display = UInt(OUTPUT, 32)
    // leds
    val stage_if = Bool(OUTPUT)
    val stage_dec = Bool(OUTPUT)
    val stage_exe = Bool(OUTPUT)
    val stage_mem = Bool(OUTPUT)
    val stage_wb = Bool(OUTPUT)
  }

  val i_cache = Module (new ICache)
  val d_cache = Module (new DCache)

  val d_path  = Module (new DataPath)
  val c_path  = Module (new ControlPath)

  d_path.io.data <> c_path.io.data
  c_path.io.ctrl <> d_path.io.ctrl

  c_path.io.dmem <> d_cache.io
  d_path.io.dmem <> d_cache.io
  c_path.io.imem <> i_cache.io
  d_path.io.imem <> i_cache.io

  // control buttons
  d_path.io.backward := io.backward
  d_path.io.execute := io.execute
  d_path.io.forward := io.forward
  // select types
  d_path.io.type_1 := io.type_1
  d_path.io.type_2 := io.type_2
  d_path.io.valueSelect := io.valueSelect
  // select register ID
  d_path.io.regID_0 := io.regID_0
  d_path.io.regID_1 := io.regID_1
  d_path.io.regID_2 := io.regID_2
  d_path.io.regID_3 := io.regID_3
  d_path.io.regID_4 := io.regID_4
  // display number
  io.display := d_path.io.display
  // mark pipeline stage
  io.stage_if := d_path.io.stage_if
  io.stage_dec := d_path.io.stage_dec
  io.stage_exe := d_path.io.stage_exe
  io.stage_mem := d_path.io.stage_mem
  io.stage_wb := d_path.io.stage_wb
}
