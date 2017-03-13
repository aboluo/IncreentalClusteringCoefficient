package com.sun;
/**
 * ����ԭͼ�ļ����ɳ�ʼͼ�ļ�������ͼ�ļ���ȫͼ�ļ�
 * ���ɺ���ļ����ߴ洢��
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class GraphPrepare {
	HashMap<Integer, Integer> nodeIdList = new HashMap<Integer, Integer>();
	int edgeNum = 0;
	BufferedReader in;

	/**
	 * ���ļ��ж������ݣ����ɱ���ߵ�HashMap��˳����ڵ����¸���Id����ȷ���ڵ��Id����
	 * 
	 * @param filename
	 *            ������ͨͼ�ļ���
	 * @return ����ߵ�HashMap
	 */
	private HashMap<Integer, ArrayList<Integer>> initLinks(
			String filename) {
		int nextValidId = 0;
		
		HashMap<Integer, ArrayList<Integer>> links = new HashMap<Integer, ArrayList<Integer>>();

		try {
			in = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] edge = line.split("\t");
				if (edge.length != 2) {
					throw new RuntimeException("File Input Error! " + line
							+ " , " + edge.length + " , " + edge[0] + " , "
							+ "\n");
				}
				int srcNode = Integer.parseInt(edge[0]);
				int desNode = Integer.parseInt(edge[1]);
				
				//Ϊ�ڵ����·���������Id
				if (nodeIdList.get(srcNode) == null) {
					nodeIdList.put(srcNode, nextValidId);
					nextValidId++;
				}
				if (nodeIdList.get(desNode) == null) {
					nodeIdList.put(desNode, nextValidId);
					nextValidId++;
				}
				int newSrcId = nodeIdList.get(srcNode);
				int newDesId = nodeIdList.get(desNode);

				if (links.get(newSrcId) == null) {
					links.put(newSrcId, new ArrayList<Integer>());
				}
				links.get(newSrcId).add(newDesId);
				this.edgeNum++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Total Node:" + nextValidId);
		return links;
	}

	private HashMap<Integer, ArrayList<Integer>> GenerateIncLinks ( HashMap<Integer, ArrayList<Integer>> links, int linkNum,	int numOfInc) {
		HashMap<Integer, ArrayList<Integer>> incLinks = new HashMap<Integer, ArrayList<Integer>>();
		int totalIncCount = numOfInc;
		double percentOfInc = (double) (numOfInc) / linkNum;
		System.out.println(totalIncCount + ":" + percentOfInc);

		while (totalIncCount > 0) {
			int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(nodeIdList.size());
			ArrayList<Integer> outlinks = links.get(i);
			if (outlinks != null) {
				if (outlinks.size() > 1) {
					int j = java.util.concurrent.ThreadLocalRandom.current()
							.nextInt(outlinks.size());
					if (incLinks.get(i) == null) {
						incLinks.put(i, new ArrayList<Integer>());
					}
					
					incLinks.get(i).add(outlinks.get(j));
					outlinks.remove(j);
					totalIncCount--;
				}
			}
		}
		return incLinks;
	}

	/**
	 * �����ӱ�����д���ļ���
	 * 
	 * @param links
	 *            �洢���ӱߵ�HashMap
	 * @param fileWithoutIncName
	 *            Ŀ���ļ���
	 */
	private void writeLinks2File(
			HashMap<Integer, ArrayList<Integer>> links,
			String filename) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			int srcNodeId;
			ArrayList<Integer> desNodeList;
			for (Entry<Integer, ArrayList<Integer>> entry: links.entrySet()) {
			     srcNodeId = entry.getKey();
			     desNodeList = entry.getValue();
			     for ( int j = 0; j < desNodeList.size(); j++ ){
						out.write(srcNodeId + "\t" + desNodeList.get(j));
						out.newLine();
					}
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateFiles ( String allFilename, String incFilename, String restFilename, int numOfInc ){
		HashMap<Integer, ArrayList<Integer>> graph = initLinks(allFilename);
		writeLinks2File(graph, allFilename);
		HashMap<Integer, ArrayList<Integer>> incLinks = GenerateIncLinks(graph, this.edgeNum, numOfInc);
		writeLinks2File(incLinks, incFilename);
		writeLinks2File(graph, restFilename);
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err
					.println("Execute : GraphPrepare <PathGraphFile> IncEdgeNum");
			return;
		}

		String pathGraphFile = args[0];
		int incEdgesNum = Integer.parseInt(args[1]);

		String pathIncGraphFile = "inc";
		String pathInitGraphFile = "init";

		GraphPrepare gp = new GraphPrepare();
		gp.generateFiles(pathGraphFile, pathIncGraphFile, pathInitGraphFile, incEdgesNum);
	}
}
