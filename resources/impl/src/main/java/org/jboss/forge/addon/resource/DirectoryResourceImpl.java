/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * A standard, build-in, resource for representing directories on the file-system.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DirectoryResourceImpl extends AbstractFileResource<DirectoryResource> implements DirectoryResource
{
   private volatile List<Resource<?>> listCache;

   public DirectoryResourceImpl(final ResourceFactory factory, final File file)
   {
      super(factory, file);

      isStale();
   }

   @Override
   public boolean create()
   {
      return mkdirs();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      if (isStale())
      {
         listCache = null;
      }

      if (listCache == null)
      {
         refresh();
         listCache = new LinkedList<>();

         File[] files = getFileOperations().listChildren(getUnderlyingResourceObject());
         if (files != null)
         {
            for (File f : files)
            {
               listCache.add(getResourceFactory().create(f));
            }
         }
      }

      return listCache;
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      return getResourceFactory().create(new File(getUnderlyingResourceObject().getAbsolutePath(), name));
   }

   @Override
   public DirectoryResource getChildDirectory(final String name) throws ResourceException
   {
      Resource<?> result = getChild(name);

      if (!(result instanceof DirectoryResource))
      {
         if (result.exists())
         {
            throw new ResourceException("The resource [" + result.getFullyQualifiedName()
                     + "] is not a DirectoryResource");
         }
         else
         {
            result = getResourceFactory().create(DirectoryResource.class,
                     new File(getUnderlyingResourceObject().getAbsoluteFile(), name));
         }
      }

      return (DirectoryResource) result;
   }

   @Override
   public DirectoryResource getOrCreateChildDirectory(String name)
   {
      DirectoryResource child = getChildDirectory(name);
      if (!child.exists())
      {
         child.mkdir();
      }
      return child;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <E, T extends Resource<E>> T getChildOfType(final Class<T> type, final String name) throws ResourceException
   {
      T result;
      Resource<?> child = getChild(name);
      if (type.isAssignableFrom(child.getClass()))
      {
         result = (T) child;
      }
      else if (child.exists())
      {
         throw new ResourceException("Requested resource [" + name + "] was not of type [" + type.getName()
                  + "], but was instead [" + child.getClass().getName() + "]");
      }
      else
      {
         E underlyingResource = (E) child.getUnderlyingResourceObject();
         result = getResourceFactory().create(type, underlyingResource);
      }
      return result;
   }

   @Override
   public DirectoryResource createTempResource()
   {
      try
      {
         File tempFile = File.createTempFile("forgetemp", "");
         tempFile.delete();
         return createFrom(tempFile);
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while creating temporary directory", e);
      }
   }

   @Override
   public DirectoryResource createFrom(final File file)
   {
      if (getFileOperations().exists(file) && !getFileOperations().existsAndIsDirectory(file))
      {
         throw new ResourceException("File reference is not a directory: " + file.getAbsolutePath());
      }
      else if (!getFileOperations().exists(file))
      {
         getFileOperations().mkdirs(file);
      }

      return getResourceFactory().create(DirectoryResource.class, file);
   }

   @Override
   public synchronized DirectoryResource getParent()
   {
      if (super.getParent() == null)
      {
         File parentFile = getUnderlyingResourceObject().getParentFile();
         if (parentFile == null)
         {
            return null;
         }

         super.setParent(createFrom(parentFile));
      }
      return super.getParent();
   }

   @Override
   public String getName()
   {
      String fileName = getUnderlyingResourceObject().getName();
      // Windows: drive letter is needed. If filename is empty, we are on a root folder
      return (OperatingSystemUtils.isWindows() && fileName.length() == 0) ? getUnderlyingResourceObject().getPath()
               : fileName;
   }

   @Override
   public boolean equals(final Object obj)
   {
      return (obj instanceof DirectoryResourceImpl)
               && ((DirectoryResourceImpl) obj).getUnderlyingResourceObject().equals(getUnderlyingResourceObject());
   }

   @Override
   public long getSize()
   {
      throw new UnsupportedOperationException("getSize not supported for DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(char[] data)
   {
      throw new UnsupportedOperationException("setContents(char[]) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(InputStream data)
   {
      throw new UnsupportedOperationException("setContents(InputStream) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(String data)
   {
      throw new UnsupportedOperationException("setContents(String) is not supported on DirectoryResource objects");
   }

   @Override
   public String getContents()
   {
      throw new UnsupportedOperationException("getContents() is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(char[] data, Charset charset)
   {
      throw new UnsupportedOperationException(
               "setContents(char[], Charset) is not supported on DirectoryResource objects");
   }

   @Override
   public DirectoryResource setContents(String data, Charset charset)
   {
      throw new UnsupportedOperationException(
               "setContents(String, Charset) is not supported on DirectoryResource objects");
   }

   @Override
   public String getContents(Charset charset)
   {
      throw new UnsupportedOperationException(
               "getContents(Charset) is not supported on DirectoryResource objects");
   }

   @Override
   public OutputStream getResourceOutputStream()
   {
      throw new UnsupportedOperationException(
               "getResourceOutputStream() is not supported on DirectoryResource objects");
   }
}