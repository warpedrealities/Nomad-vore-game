
function fire(script,miss)
	target=script:getTarget()	
	emitter=script:getEmitter()

	if not miss then
		distance=emitter:getDistance(target)	
		script:makeBeam(0,emitter,0,3,true,1,distance)
		angle=script:getAngle(emitter.x,emitter.y,target.x,target.y)	
		effect=script:getEffect(0)	
		effect:setRotation(angle)

		effect:setVelocity(distance,0)
		script:resolve()
		script:makeSprite(1,script:getTarget(),4,4,false,1)	
		script:getEffect(1):setAnimationSpeed(4)	
	else
		script:makeBeam(0,emitter,0,3,true,1,32)
		angle=script:getAngle(emitter.x,emitter.y,target.x,target.y)	
		angle=angle+(math.random(0,2)/10)-0.1
		effect=script:getEffect(0)	
		effect:setRotation(angle)	
		target=target:replicate()
		effect:setRotation(angle)
	end
	effect:setAnimationSpeed(2)
end

function sustain(script,miss,scriptClock)
	if not miss then
		target=script:getTarget()	
		emitter=script:getEmitter()	
		distance=emitter:getDistance(target)		
		angle=script:getAngle(emitter.x,emitter.y,target.x,target.y)
		effect=script:getEffect(0)	
		effect:setVelocity(distance,0)	
		effect:setRotation(angle)
	end
	
	if (scriptClock>0.25) then
		script:removeEffect(0)
		return true;
	end
end

function main(script, scriptClock,miss)  

	if script:getEffect(0) == nil then
		fire(script,miss)
	else
		return sustain(script,miss,scriptClock)
	end

	return false;
end  
