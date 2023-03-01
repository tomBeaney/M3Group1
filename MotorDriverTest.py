import ThunderBorg3 as ThunderBorg

TB = ThunderBorg.ThunderBorg()
TB.Init()
voltageIn = 9.0
voltageOut = 9.0 * 0.95

TB.GetMotor1()
TB.GetMotor2()
TB.SetLed1(1, 1, 1)
TB.SetMotor2(1)
TB.SetMotor1(1)

#TB.MotorsOff()
TB.GetBatteryReading()
