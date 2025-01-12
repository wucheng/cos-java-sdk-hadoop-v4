/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package com.qcloud.cos.demo;

import com.qcloud.cos.*;
import com.qcloud.cos.meta.FileAuthority;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.CreateFolderRequest;
import com.qcloud.cos.request.DelFileRequest;
import com.qcloud.cos.request.DelFolderRequest;
import com.qcloud.cos.request.ListFolderRequest;
import com.qcloud.cos.request.MoveFileRequest;
import com.qcloud.cos.request.StatFileRequest;
import com.qcloud.cos.request.StatFolderRequest;
import com.qcloud.cos.request.UpdateFileRequest;
import com.qcloud.cos.request.UpdateFolderRequest;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.request.UploadSliceFileRequest;


/**
 * @author chengwu cos Demo代码
 */
public class Demo {

	public static void main(String[] args) {
		
		// 设置用户属性, 包括appid, secretId和SecretKey
		// 这些属性可以通过cos控制台获取(https://console.qcloud.com/cos)

		
		int appId = 1251001000;
		String secretId = "xxxxxxxxx";
		String secretKey = "xxxxxxxxxxxxxxxxxxx";
		// 初始化cosClient
		COSClient cosClient = new COSClient(appId, secretId, secretKey);
		// 设置要操作的bucket
		String bucketName = "mytest";
		
		ListFolderRequest listFolderRequest = new ListFolderRequest(bucketName, "/");
		String listFolderRet = cosClient.listFolder(listFolderRequest);
		System.out.println("list folder ret:" + listFolderRet);
		///////////////////////////////////////////////////////////////
		// 文件操作 													 //
		///////////////////////////////////////////////////////////////
		// 1. 上传文件(默认不覆盖)
		// 将本地的local_file_1.txt上传到bucket下的根分区下,并命名为sample_file.txt
		// 默认不覆盖, 如果cos上已有文件, 则返回错误
		String cosFilePath = "/sample_file.txt";
		String localFilePath1 = "src/test/resources/local_file_1.txt";
		UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, cosFilePath, localFilePath1);
		String uploadFileRet = cosClient.uploadFile(uploadFileRequest);
		System.out.println("upload file ret:" + uploadFileRet);
		
		// 上传大文件
		String cosBigFilePath = "/rabbitliu_bigfile6.txt";
		String localBigFilePath = "src/test/resources/bigfile.txt";
		UploadFileRequest uploadBigFileRequest = new UploadFileRequest(bucketName, cosBigFilePath, localBigFilePath);
		UploadSliceFileRequest uploadSliceRequest = new UploadSliceFileRequest(uploadBigFileRequest);
		uploadSliceRequest.setInsertOnly(InsertOnly.OVER_WRITE);
		//uploadSliceRequest.setSerialUpload(false);
		String uploadBigFileRet = cosClient.uploadSliceFile(uploadSliceRequest);
		System.out.println("upload big file ret: " + uploadBigFileRet);
		
		// 2. 上传文件(覆盖)
		// 将本地的local_file_2.txt上传到bucket下的根分区下,并命名为sample_file.txt
		String localFilePath2 = "src/test/resources/local_file_2.txt";
		UploadFileRequest overWriteFileRequest = new UploadFileRequest(bucketName, cosFilePath, localFilePath2);
		overWriteFileRequest.setInsertOnly(InsertOnly.OVER_WRITE);
		String overWriteFileRet = cosClient.uploadFile(overWriteFileRequest);
		System.out.println("overwrite file ret:" + overWriteFileRet);

		// 3. 获取文件属性
		StatFileRequest statFileRequest = new StatFileRequest(bucketName, cosFilePath);
		String statFileRet = cosClient.statFile(statFileRequest);
		System.out.println("stat file ret:" + statFileRet);

		// 4. 更新文件属性
		UpdateFileRequest updateFileRequest = new UpdateFileRequest(bucketName, cosFilePath);
		updateFileRequest.setBizAttr("测试目录");
		updateFileRequest.setAuthority(FileAuthority.WPRIVATE);
		updateFileRequest.setCacheControl("no cache");
		updateFileRequest.setContentDisposition("cos_sample.txt");
		updateFileRequest.setContentLanguage("english");
		updateFileRequest.setContentType("application/json");
		updateFileRequest.setXCosMeta("x-cos-meta-xxx", "xxx");
		updateFileRequest.setXCosMeta("x-cos-meta-yyy", "yyy");
		String updateFileRet = cosClient.updateFile(updateFileRequest);
		System.out.println("update file ret:" + updateFileRet);

		// 5. 更新文件后再次获取属性
		statFileRet = cosClient.statFile(statFileRequest);
		System.out.println("stat file ret:" + statFileRet);
		
		listFolderRequest = new ListFolderRequest(bucketName, "/");
		listFolderRet = cosClient.listFolder(listFolderRequest);
		System.out.println("list folder ret:" + listFolderRet);

		// 6. 删除文件
		DelFileRequest delFileRequest = new DelFileRequest(bucketName, cosFilePath);
		String delFileRet = cosClient.delFile(delFileRequest);
		System.out.println("del file ret:" + delFileRet);
		// 删除大文件
		DelFileRequest delBigFileRequest = new DelFileRequest(bucketName, cosBigFilePath);
		delFileRet = cosClient.delFile(delBigFileRequest);
		System.out.println("del big file ret: " + delFileRet);

		///////////////////////////////////////////////////////////////
		// 目录操作 //
		///////////////////////////////////////////////////////////////
		// 1. 生成目录, 目录名为sample_folder
		String cosFolderPath = "/a/b/c/xxsample_folder/";
		CreateFolderRequest createFolderRequest = new CreateFolderRequest(bucketName, cosFolderPath);
		String createFolderRet = cosClient.createFolder(createFolderRequest);
		System.out.println("create folder ret:" + createFolderRet);

		// 2. 更新目录的biz_attr属性
		UpdateFolderRequest updateFolderRequest = new UpdateFolderRequest(bucketName, cosFolderPath);
		updateFolderRequest.setBizAttr("这是一个测试目录");
		String updateFolderRet = cosClient.updateFolder(updateFolderRequest);
		System.out.println("update folder ret:" + updateFolderRet);
        
        // 6.1 move文件，从/sample_file.txt移动为./sample_file.txt.bak
        String dstFilePath = cosFilePath + ".bak";
        MoveFileRequest moveRequest = new MoveFileRequest(bucketName, cosFilePath, dstFilePath);
        String moveFileRet = cosClient.moveFile(moveRequest);
        System.out.println("first move file ret:" + moveFileRet);
        // 6.2 在从/sample_file.txt.bak移动为/sample_file.txt
        moveRequest = new MoveFileRequest(bucketName, dstFilePath, cosFilePath);
        moveFileRet = cosClient.moveFile(moveRequest);
        System.out.println("second move file ret:" + moveFileRet);

		// 3. 获取目录属性
		StatFolderRequest statFolderRequest = new StatFolderRequest(bucketName, cosFolderPath);
		String statFolderRet = cosClient.statFolder(statFolderRequest);
		System.out.println("stat folder ret:" + statFolderRet);

		// 4. list目录, 获取目录下的成员
		listFolderRequest = new ListFolderRequest(bucketName, "/");
		listFolderRequest.setDelimiter("");
		listFolderRet = cosClient.listFolder(listFolderRequest);
		System.out.println("list folder ret:" + listFolderRet);

		// 5. 删除目录
		DelFolderRequest delFolderRequest = new DelFolderRequest(bucketName, cosFolderPath);
		String delFolderRet = cosClient.delFolder(delFolderRequest);
		System.out.println("del folder ret:" + delFolderRet);

		// 关闭释放资源
		cosClient.shutdown();
		System.out.println("shutdown!");
		
	}
}
