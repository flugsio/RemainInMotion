package me.planetguy.remaininmotion ;

import me.planetguy.remaininmotion.util.Reflection;

public abstract class CarriageRenderCache
{
	public static class RenderRecord
	{
		public int PrimaryPassDisplayList ;

		public int SecondaryPassDisplayList ;
	}

	public static java . util . TreeMap < BlockPosition , RenderRecord > Cache = new java . util . TreeMap < BlockPosition , RenderRecord > ( ) ;

	public static void Render ( net . minecraft . client . renderer . RenderBlocks BlockRenderer , BlockRecordSet Blocks , BlockRecordSet TileEntities , int Pass )
	{
		net . minecraft . client . renderer . RenderHelper . disableStandardItemLighting ( ) ;

		{
			Render . ResetBoundTexture ( ) ;

			Render . BindBlockTexture ( ) ;

			Render . PushMatrix ( ) ;

			net . minecraft . client . renderer . Tessellator . instance . startDrawingQuads ( ) ;

			for ( BlockRecord Record : Blocks )
			{
				try
				{
					if ( ! Record.block . canRenderInPass ( Pass ) )
					{
						continue ;
					}
				}
				catch ( Throwable Throwable )
				{
					Throwable . printStackTrace ( ) ;

					continue ;
				}

				try
				{
					BlockRenderer . renderBlockByRenderType ( Record.block, Record . X , Record . Y , Record . Z ) ;
				}
				catch ( Throwable Throwable )
				{
					Throwable . printStackTrace ( ) ;
				}
			}

			try
			{
				net . minecraft . client . renderer . Tessellator . instance . draw ( ) ;
			}
			catch ( Throwable Throwable )
			{
				Throwable . printStackTrace ( ) ;
			}

			Render . PopMatrix ( ) ;
		}

		net . minecraft . client . renderer . RenderHelper . enableStandardItemLighting ( ) ;

		{
			Render . PushMatrix ( ) ;

			Render . Translate
			(
				net . minecraft . client . renderer . tileentity . TileEntityRendererDispatcher . instance . staticPlayerX ,
				net . minecraft . client . renderer . tileentity . TileEntityRendererDispatcher . instance . staticPlayerY ,
				net . minecraft . client . renderer . tileentity . TileEntityRendererDispatcher . instance . staticPlayerZ
			) ;

			for ( BlockRecord Record : TileEntities )
			{
				if ( ! Record . Entity . shouldRenderInPass ( Pass ) )
				{
					continue ;
				}

				Render . ResetBoundTexture ( ) ;

				Render . PushMatrix ( ) ;

				try
				{
					net . minecraft . client . renderer . tileentity . TileEntityRendererDispatcher . instance . renderTileEntity ( Record . Entity , 0 ) ;
				}
				catch ( Throwable Throwable )
				{
					Throwable . printStackTrace ( ) ;
				}

				if ( (Boolean)Reflection.stealField(net . minecraft . client . renderer . Tessellator . instance, "isDrawing" ))
						
				{
					try
					{
						net . minecraft . client . renderer . Tessellator . instance . draw ( ) ;
					}
					catch ( Throwable Throwable )
					{
						Throwable . printStackTrace ( ) ;
					}
				}

				Render . PopMatrix ( ) ;
			}

			Render . PopMatrix ( ) ;

			try
			{
				net . minecraft . client . renderer . Tessellator . instance . draw ( ) ;
			}
			catch ( Throwable Throwable )
			{
			}

			Render . ResetBoundTexture ( ) ;
		}
	}

	public static void Assemble ( BlockRecordSet Blocks , BlockRecordSet TileEntities , net . minecraft . world . World World , BlockPosition Key )
	{
		if ( Cache . containsKey ( Key ) )
		{
			return ;
		}

		net . minecraft . client . renderer . RenderBlocks BlockRenderer = new net . minecraft . client . renderer . RenderBlocks ( World ) ;

		RenderRecord RenderRecord = new RenderRecord ( ) ;

		RenderRecord . PrimaryPassDisplayList = Render . InitializeDisplayList ( ) ;

		Render ( BlockRenderer , Blocks , TileEntities , 0 ) ;

		Render . FinalizeDisplayList ( ) ;

		RenderRecord . SecondaryPassDisplayList = Render . InitializeDisplayList ( ) ;

		Render ( BlockRenderer , Blocks , TileEntities , 1 ) ;

		Render . FinalizeDisplayList ( ) ;

		Cache . put ( Key , RenderRecord ) ;
	}

	public static Integer Lookup ( BlockPosition Key )
	{
		int Pass = net . minecraftforge . client . MinecraftForgeClient . getRenderPass ( ) ;

		RenderRecord RenderRecord = Cache . get ( Key ) ;

		if ( RenderRecord == null )
		{
			return ( null ) ;
		}

		if ( Pass == 0 )
		{
			return ( RenderRecord . PrimaryPassDisplayList ) ;
		}

		if ( Pass == 1 )
		{
			return ( RenderRecord . SecondaryPassDisplayList ) ;
		}

		return ( null ) ;
	}

	public static void Release ( BlockPosition Key )
	{
		if ( Key == null )
		{
			return ;
		}

		RenderRecord RenderRecord = Cache . remove ( Key ) ;

		if ( RenderRecord != null )
		{
			Render . ReleaseDisplayList ( RenderRecord . PrimaryPassDisplayList ) ;

			Render . ReleaseDisplayList ( RenderRecord . SecondaryPassDisplayList ) ;
		}
	}
}
