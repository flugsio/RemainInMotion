package me.planetguy.remaininmotion ;

import net.minecraft.tileentity.TileEntity;

public class CarriageEngineEntity extends CarriageDriveEntity
{
	@Override
	public CarriagePackage GeneratePackage ( TileEntity carriage , Directions CarriageDirection , Directions MotionDirection ) throws CarriageMotionException
	{
		CarriagePackage Package = new CarriagePackage ( this , carriage , MotionDirection ) ;

		Package . AddBlock ( Package . DriveRecord ) ;

		if ( MotionDirection != CarriageDirection )
		{
			Package . AddPotentialObstruction ( Package . DriveRecord . NextInDirection ( MotionDirection ) ) ;
		}

		TEAccessUtil.fillPackage(Package, carriage ) ;
		
		Package . Finalize ( ) ;

		return ( Package ) ;
	}

	@Override
	public boolean Anchored ( )
	{
		return ( false ) ;
	}
}
