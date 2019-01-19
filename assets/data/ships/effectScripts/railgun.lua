
function launch(script,miss)
	print("launch")
		origin=script:getOrigin()
		emitter=script:getEmitter()
		target=script:getTarget()
		
		distance=emitter:getDistance(target)
		target=script:leadTarget(distance/128)
		if ( miss == true ) then
			target.x=target.x+math.random(0,2)-1
			target.y=target.y+math.random(0,2)-1
		end
		script:makeSprite(0,emitter:replicate(),0,1,true,1)
		script:makeSprite(1,emitter:replicate(),1,3,false,2)	
		
		velocity=target:replicate()
		velocity.x=target.x-origin.x
		velocity.y=target.y-origin.y
		velocity:normalize()
		velocity.x=velocity.x*64
		velocity.y=velocity.y*64
		angle=script:getAngle(emitter.x,emitter.y,target.x,target.y)

		e=script:getEffect(0)	
		e:setRotation(-angle)
		e:setVelocity(velocity.x,velocity.y)
		e=script:getEffect(1)
		e:setRotation(-angle)


end	

function spark(script, scriptClock, effect) 
	i=math.floor((scriptClock*30)+3)
	print(i)
	if (script:getEffect(i)==nil) then
		script:makeSprite(i,effect:getPosition():replicate(),4,4,false,1)
		script:getEffect(i):setAnimationSpeed(1)			
	return
	end
end

function fly(script,miss,scriptClock)

	if ( miss == false ) then
	
	distance=script:getEffect(0):getPosition():getDistance(script:getOrigin())
	range=script:getOrigin():getDistance(script:getTarget())
	if (distance>range) then
		script:resolve()
		script:makeSprite(2,script:getTarget():replicate(),8,4,false,2)	
		angle=script:getAngle(script:getOrigin().x,script:getOrigin().y,script:getTarget().x,script:getTarget().y)
		e=script:getEffect(2)	
		e:setRotation(-angle)
		script:getEffect(2):setAnimationSpeed(4)
		script:removeEffect(0)	
		return true;	
	else
		spark(script,scriptClock,script:getEffect(0))
	end
		spark(script,scriptClock,script:getEffect(0))
		if (scriptClock>4) then
			script:removeEffect(0)	
			return true;
		end
		
	end

	if (miss and scriptClock>4) then
		script:removeEffect(0)
		return true;
	end
	return false;
end


function main(script, scriptClock,miss)  

	if script:getEffect(0) == nil then
		launch(script,miss)
	else
		return fly(script,miss,scriptClock)	
	end

	return false;
end  
