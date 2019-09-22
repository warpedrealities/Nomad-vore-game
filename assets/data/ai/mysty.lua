
function main(controllable, sense, script)  
	if (controllable:getValue(0)==0) then
		sense:drawText("Mysty: Oh my an intruder, whatever are you going to do to me?")
		controllable:setValue(0,1)
	end
end  